package com.library.polargx

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.net.Uri
import android.os.Bundle
import androidx.window.layout.WindowMetricsCalculator
import com.library.polargx.configuration.Configuration
import com.library.polargx.di.polarModule
import com.library.polargx.extension.getDeviceModel
import com.library.polargx.extension.getDeviceName
import com.library.polargx.extension.getIP4Address
import com.library.polargx.extension.getIP6Address
import com.library.polargx.extension.getManufacturer
import com.library.polargx.extension.getOsVersion
import com.library.polargx.extension.getSdkVersion
import com.library.polargx.helpers.FileStorage
import com.library.polargx.listener.PolarInitListener
import com.library.polargx.logger.Logger
import com.library.polargx.model.configs.ConfigsModel
import com.library.polargx.repository.event.EventRepository
import com.library.polargx.repository.event.model.EventModel
import com.library.polargx.repository.link.LinkRepository
import com.library.polargx.repository.link.model.link.LinkDataModel
import com.library.polargx.repository.link.remote.api.click.LinkClickRequest
import com.library.polargx.repository.link.remote.api.track.LinkTrackRequest
import com.library.polargx.utils.DateTimeUtils
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import java.time.Instant
import java.util.Calendar
import java.util.UUID

typealias OnLinkClickHandler = (link: String?, data: Map<String, Any>?, error: Exception?) -> Unit

//TODO: PolarApp initializing should create a singleton (current app / shared in iOS)
//TODO: can you add
class PolarApp(
    private val application: Application,
    private val appId: String,
    private val onLinkClickHandler: OnLinkClickHandler
) : KoinComponent {

    private val eventRepository: EventRepository by inject()
    private val linkRepository: LinkRepository by inject()

    private lateinit var mKronosClock: KronosClock

    private var mLastLink: LinkDataModel? = null

    /**
     * The storage location to save user data and events (belong to SDK).
     */
    private val appDirectory by lazy {
        FileStorage.getSDKDirectory(application).appendingSubDirectory(appId)
    }

    private var currentUserSession: UserSession? = null
    private var otherUserSessions = mutableListOf<UserSession>()

    companion object {
        const val TAG = ">>>Polar"

        @SuppressLint("StaticFieldLeak")
        private var instance: PolarApp? = null
        private var mConfigs: ConfigsModel? = null

        var isDevelopmentEnabled = false
        var isLoggingEnabled = false

        private var mInitAppJob: Job? = null
        private var mGetLinkJob: Job? = null

        private var mLastUri: Uri? = null

        @SuppressLint("StaticFieldLeak")
        private var mLastActivity: Activity? = null
        private var mLastListener: PolarInitListener? = null
        private var isReInitializing: Boolean? = null

        fun getConfigs(): ConfigsModel? {
            return mConfigs
        }

        private fun isAppInitializing(): Boolean {
            return mInitAppJob?.isActive == true
        }

        //TODO: current instance of PolarApp to access (not null) - shared in SwiftSDK
        //TODO: call initialize function to make PolarApp instance immediately
        //TODO: don't need onInitFinished -

        fun initialize(
            application: Application,
            appId: String,
            apiKey: String,
            onLinkClickHandler: OnLinkClickHandler,
            onInitFinished: () -> Unit
        ) {
            if (mInitAppJob?.isActive == true) {
                mInitAppJob?.cancel()
                mInitAppJob = null
            }
            mInitAppJob = CoroutineScope(Dispatchers.IO).launch {
                mConfigs = ConfigsModel(appId = appId, apiKey = apiKey)
                if (instance == null) {
                    instance = PolarApp(
                        application = application,
                        appId = appId,
                        onLinkClickHandler = onLinkClickHandler
                    ).apply {
                        startInject()
                        mKronosClock = AndroidClockFactory.createKronosClock(application)
                        mKronosClock.sync()
                    }
                    onInitFinished()
                }
                instance?.startInitializingApp()
                if (mLastUri != null) {
                    instance?.init()
                } else {
                    mLastListener?.onInitFinished(null, null)
                }
            }
        }

        fun bind(
            activity: Activity?,
            uri: Uri?,
            listener: PolarInitListener
        ) {
            Logger.d(TAG, "init: uri=$uri")
            if (mGetLinkJob?.isActive == true) {
                mGetLinkJob?.cancel()
            }
            mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
                isReInitializing = false
                mLastUri = uri
                mLastActivity = activity
                mLastListener = listener
                if (isAppInitializing()) return@launch
                instance?.init()
            }
        }

        fun reBind(
            activity: Activity?,
            uri: Uri?,
            listener: PolarInitListener
        ) {
            Logger.d(TAG, "reInit: uri=$uri")
            if (mGetLinkJob?.isActive == true) {
                mGetLinkJob?.cancel()
            }
            mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
                isReInitializing = true
                mLastUri = uri
                mLastActivity = activity
                mLastListener = listener
                if (isAppInitializing()) return@launch
                instance?.reInit()
            }
        }

        fun updateUser(userID: String?, attributes: Map<String, String>?) {
            instance?.setUser(userID, attributes)
        }

        fun trackEvent(name: String, attributes: Map<String, String>?) {
            instance?.trackEvent(name, attributes)
        }
    }

    private fun isKoinStarted(): Boolean {
        return GlobalContext.getOrNull() != null
    }

    private fun startInject() {
        if (isKoinStarted()) {
            loadKoinModules(polarModule)
            return
        }
        startKoin {
            androidLogger()
            androidContext(application)
            modules(polarModule)
        }
    }

    fun startInitializingApp() {
        startTrackingAppLifeCycle()

        val pendingEventFiles = FileStorage
            .listFiles(appDirectory)
            .filter { it.startsWith("events_") }
        startResolvingPendingEvents(pendingEventFiles)
    }

    /**
     * Set userID and attributes:
     * - Create current user session if needed
     * - Backup user session into the otherUserSessions to keep running for sending events
     */
    private fun setUser(userID: String?, attributes: Map<String, String>?) {
        currentUserSession?.let { userSession ->
            if (userSession.userID != userID) {
                currentUserSession = null
                otherUserSessions.add(userSession)
            }
        }

        if (currentUserSession == null && userID != null) {
            val name = "events_${Instant.now().epochSecond}_${UUID.randomUUID()}.json"
            val file = appDirectory.file(name)
            Logger.d(TAG, "TrackingEvents stored in `${file.absolutePath}`")

            currentUserSession = UserSession(
                organizationUnid = appId,
                userID = userID,
                trackingFileStorage = file
            )

            currentUserSession?.setAttributes(attributes ?: emptyMap())
        }
    }

    private fun trackEvent(name: String?, attributes: Map<String, String>?) {
        CoroutineScope(Dispatchers.Main).launch {
            val date = DateTimeUtils.calendarToString(
                source = Calendar.getInstance().apply {
                    timeInMillis = mKronosClock.getCurrentTimeMs()
                },
                format = Constants.DateTime.DEFAULT_DATE_FORMAT,
                timeZone = Constants.DateTime.utcTimeZone,
            )
            currentUserSession?.trackEvent(name, date, attributes)
        }
    }

    private fun startTrackingAppLifeCycle() {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
                instance?.trackEvent(
                    name = EventModel.Type.APP_OPEN,
                    attributes = mapOf()
                )
            }

            override fun onActivityResumed(activity: Activity) {
                instance?.trackEvent(
                    name = EventModel.Type.APP_ACTIVE,
                    attributes = mapOf()
                )
            }

            override fun onActivityPaused(activity: Activity) {
                instance?.trackEvent(
                    name = EventModel.Type.APP_INACTIVE,
                    attributes = mapOf()
                )
            }

            override fun onActivityStopped(activity: Activity) {
                instance?.trackEvent(
                    name = EventModel.Type.APP_CLOSE,
                    attributes = mapOf()
                )
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                instance?.trackEvent(
                    name = EventModel.Type.APP_TERMINATE,
                    attributes = mapOf()
                )
            }
        })
    }

    private fun startResolvingPendingEvents(pendingEventFiles: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            pendingEventFiles.forEach { pendingEventFile ->
                val file = appDirectory.file(pendingEventFile)
                val eventQueue = TrackingEventQueue(file)

                if (eventQueue.events.isEmpty()) {
                    FileStorage.remove(pendingEventFile, appDirectory)
                    return@forEach
                }

                eventQueue.setReady()
                eventQueue.sendEventsIfNeeded()

                if (eventQueue.events.isEmpty()) {
                    FileStorage.remove(pendingEventFile, appDirectory)
                }
            }
        }
    }

    suspend fun init() {
        handleFetchLinkData(activity = mLastActivity, uri = mLastUri)
    }

    suspend fun reInit() {
        handleFetchLinkData(activity = mLastActivity, uri = mLastUri)
    }

    private suspend fun handleFetchLinkData(activity: Activity?, uri: Uri?) {
        if (activity == null) return
        val supportedBaseDomains = Configuration.Env.supportedBaseDomains
        val domain = uri?.host ?: ""
        if (!domain.endsWith(supportedBaseDomains)) {
            Logger.d(TAG, "handleFetchLinkData: Invalid domain! domain=$domain")
            mLastListener?.onInitFinished(null, null)
            return
        }
        val subDomain = domain.replace(supportedBaseDomains, "")
        val path = uri?.path?.replace("/", "")
        val isFirstTimeLaunch = eventRepository.isFirstTimeLaunch(
            activity,
            mKronosClock.getCurrentTimeMs()
        )
        val now = Calendar.getInstance().apply {
            timeInMillis = mKronosClock.getCurrentTimeMs()
        }
        val clickTime = DateTimeUtils.calendarToString(
            now,
            Constants.DateTime.DEFAULT_DATE_FORMAT,
            Constants.DateTime.utcTimeZone,
        )
        if (!uri?.path.isNullOrEmpty()) {
            try {
                val getLinkResponse = linkRepository.fetchLinkData(
                    domain = subDomain,
                    slug = path
                )
                mLastLink = getLinkResponse.data?.sdkLinkData?.toExternal()

                val trackRequest = LinkTrackRequest(
                    domain = subDomain,
                    slug = path,
                    trackType = LinkTrackRequest.TrackType.APP_CLICK,
                    clickTime = clickTime,
                    fingerprint = LinkTrackRequest.Fingerprint.ANDROID_SDK,
                    deviceData = mapOf(),
                    additionalData = mapOf(),
                )
                val clid = uri?.getQueryParameter("__clid")
                if (clid.isNullOrEmpty()) {
                    val trackResponse = linkRepository.track(trackRequest)
                    val linkClickUnid = trackResponse.data?.linkClick?.unid
                    val request = LinkClickRequest(sdkUsed = true)
                    linkRepository.linkClick(linkClickUnid, request)
                } else {
                    val request = LinkClickRequest(sdkUsed = true)
                    linkRepository.linkClick(clid, request)
                }

                mLastListener?.onInitFinished(mLastLink?.data, null)
                onLinkClickHandler(uri.toString(), mLastLink?.data, null)
            } catch (e: Exception) {
                mLastListener?.onInitFinished(null, e)
                onLinkClickHandler(uri.toString(), null, e)
            }
            return
        }
        if (isFirstTimeLaunch && uri?.path.isNullOrEmpty()) {
            val windowMetrics = WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)
            val width = windowMetrics.bounds.width()
            val height = windowMetrics.bounds.height()

            // Get density using Resources
            val metrics = activity.resources?.displayMetrics
            val density = metrics?.density
            val densityDpi = metrics?.densityDpi

            Logger.d(
                TAG, "initSession: " +
                        "\nIP4Address=${application.getIP4Address()}" +
                        "\nIP6Address=${application.getIP6Address()}" +
                        "\nosVersion=${application.getOsVersion()}" +
                        "\nsdkVersion=${application.getSdkVersion()}" +
                        "\ndeviceModel=${application.getDeviceModel()}" +
                        "\nmanufacturer=${application.getManufacturer()}" +
                        "\ndeviceName=${application.getDeviceName()}" +
                        "\nwindow.width=${width}" +
                        "\nwindow.height=${height}" +
                        "\nwindow.density=${density}" +
                        "\nwindow.densityDpi=${densityDpi}"
            )
        }
    }
}
package com.library.polargx

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
import com.library.polargx.logger.Logger
import com.library.polargx.repository.event.EventRepository
import com.library.polargx.repository.event.model.EventModel
import com.library.polargx.repository.link.LinkRepository
import com.library.polargx.repository.link.model.link.LinkDataModel
import com.library.polargx.repository.link.remote.api.click.LinkClickRequest
import com.library.polargx.repository.link.remote.api.track.LinkTrackRequest
import com.library.polargx.utils.DateTimeUtils
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
import java.util.Date
import java.util.UUID

typealias OnLinkClickHandler = (link: String?, data: Map<String, Any>?, error: Exception?) -> Unit

class PolarApp private constructor(
    val appId: String,
    val apiKey: String,
    val onLinkClickHandler: OnLinkClickHandler
) : KoinComponent {

    private val eventRepository: EventRepository by inject()
    private val linkRepository: LinkRepository by inject()
    private val application: Application by inject()

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

        var isDevelopmentEnabled = false
        var isLoggingEnabled = false

        private var mGetLinkJob: Job? = null
        private var mLastUri: Uri? = null

        @Volatile
        private var _shared: PolarApp? = null

        val shared: PolarApp
            get() = _shared ?: synchronized(this) { // Ensure thread-safe
                _shared ?: error("PolarApp hasn't been initialized!")
            }

        fun initialize(
            appId: String,
            apiKey: String,
            onLinkClickHandler: OnLinkClickHandler
        ) {
            _shared = PolarApp(
                appId = appId,
                apiKey = apiKey,
                onLinkClickHandler = onLinkClickHandler
            )

            shared.startInject()
            shared.startInitializingApp()
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

    private fun startInitializingApp() {
        startTrackingAppLifeCycle()

        val pendingEventFiles = FileStorage
            .listFiles(appDirectory)
            .filter { it.startsWith("events_") }
        startResolvingPendingEvents(pendingEventFiles)
    }

    fun bind(uri: Uri?) {
        Logger.d(TAG, "bind: uri: $uri")
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            mLastUri = uri
            init()
        }
    }

    fun reBind(uri: Uri?) {
        Logger.d(TAG, "reBind: uri: $uri")
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            mLastUri = uri
            reInit()
        }
    }

    /**
     * Set userID and attributes:
     * - Create current user session if needed
     * - Backup user session into the otherUserSessions to keep running for sending events
     */
    fun updateUser(userID: String?, attributes: Map<String, String>?) {
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

    fun trackEvent(name: String?, attributes: Map<String, String>?) {
        CoroutineScope(Dispatchers.Main).launch {
            val date = DateTimeUtils.calendarToString(
                source = Calendar.getInstance(),
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
                trackEvent(
                    name = EventModel.Type.APP_OPEN,
                    attributes = mapOf()
                )
            }

            override fun onActivityResumed(activity: Activity) {
                trackEvent(
                    name = EventModel.Type.APP_ACTIVE,
                    attributes = mapOf()
                )
            }

            override fun onActivityPaused(activity: Activity) {
                trackEvent(
                    name = EventModel.Type.APP_INACTIVE,
                    attributes = mapOf()
                )
            }

            override fun onActivityStopped(activity: Activity) {
                trackEvent(
                    name = EventModel.Type.APP_CLOSE,
                    attributes = mapOf()
                )
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                trackEvent(
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

    private fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            handleOpeningURL(mLastUri)
        }
    }

    private fun reInit() {
        CoroutineScope(Dispatchers.IO).launch {
            handleOpeningURL(mLastUri)
        }
    }

    private suspend fun handleOpeningURL(uri: Uri?) {
        val supportedBaseDomains = Configuration.Env.supportedBaseDomains
        val domain = uri?.host
        if (domain?.endsWith(supportedBaseDomains) != true) {
            Logger.d(TAG, "Invalid domain: $domain")
            return
        }
        val subDomain = domain.replace(supportedBaseDomains, "")
        val path = uri.path?.replace("/", "")
        val context = application.applicationContext
        val isFirstTimeLaunch = eventRepository.isFirstTimeLaunch(
            context,
            System.currentTimeMillis()
        )
        val clickTime = DateTimeUtils.dateToString(
            Date(),
            Constants.DateTime.DEFAULT_DATE_FORMAT,
            Constants.DateTime.utcTimeZone,
        )
        if (!uri.path.isNullOrEmpty()) {
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
                val clid = uri.getQueryParameter("__clid")
                if (clid.isNullOrEmpty()) {
                    val trackResponse = linkRepository.track(trackRequest)
                    val linkClickUnid = trackResponse.data?.linkClick?.unid
                    val request = LinkClickRequest(sdkUsed = true)
                    linkRepository.linkClick(linkClickUnid, request)
                } else {
                    val request = LinkClickRequest(sdkUsed = true)
                    linkRepository.linkClick(clid, request)
                }
                onLinkClickHandler(uri.toString(), mLastLink?.data, null)
            } catch (e: Exception) {
                onLinkClickHandler(uri.toString(), null, e)
            }
            return
        }
        if (isFirstTimeLaunch && uri.path.isNullOrEmpty()) {
            val windowMetrics = WindowMetricsCalculator
                .getOrCreate()
                .computeCurrentWindowMetrics(context)
            val width = windowMetrics.bounds.width()
            val height = windowMetrics.bounds.height()

            // Get density using Resources
            val metrics = context.resources?.displayMetrics
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
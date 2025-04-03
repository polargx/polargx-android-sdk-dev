package com.library.polargx

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.window.layout.WindowMetricsCalculator
import com.library.polargx.api.ApiService
import com.library.polargx.api.track_link.TrackLinkClickRequest
import com.library.polargx.api.update_link.UpdateLinkClickRequest
import com.library.polargx.di.polarModule
import com.library.polargx.extension.getDeviceModel
import com.library.polargx.extension.getDeviceName
import com.library.polargx.extension.getIP4Address
import com.library.polargx.extension.getIP6Address
import com.library.polargx.extension.getManufacturer
import com.library.polargx.extension.getOsVersion
import com.library.polargx.extension.getSdkVersion
import com.library.polargx.helpers.DateTimeUtils
import com.library.polargx.helpers.FileStorage
import com.library.polargx.helpers.Logger
import com.library.polargx.listener.PolarInitListener
import com.library.polargx.models.LinkDataModel
import com.library.polargx.models.MapModel
import com.library.polargx.models.TrackEventModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import java.util.Date
import java.util.UUID

typealias OnLinkClickHandler = (link: String?, data: Map<String, Any>?, error: Exception?) -> Unit
typealias UntrackedEvent = Triple<String, String, Map<String, Any>>

private class InternalPolarApp(
    val appId: String,
    override var apiKey: String,
    val onLinkClickHandler: OnLinkClickHandler
) : PolarApp(), KoinComponent {

    private val apiService by inject<ApiService>()
    private val application by inject<Application>()

    private val maxCapacity = 100

    private var mLastLink: LinkDataModel? = null
    private var mLastListener: PolarInitListener? = null

    private var mGetLinkJob: Job? = null

    /**
     * The storage location to save user data and events (belong to SDK).
     */
    private val appDirectory by lazy {
        FileStorage.getSDKDirectory(application).appendingSubDirectory(appId)
    }

    private var currentUserSession: UserSession? = null
    private val otherUserSessions = mutableListOf<UserSession>()
    private var pendingEvents = arrayListOf<UntrackedEvent>()

    init {
        if (apiKey.startsWith("dev_")) {
            apiKey = apiKey.substring(4)
            Configuration.Env = DevEnvConfiguration()
        }

        pendingEvents.ensureCapacity(maxCapacity)

        startInject()
        startInitializingApp()
    }

    private fun isKoinStarted(): Boolean {
        return GlobalContext.getOrNull() != null
    }

    fun startInject() {
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

    override fun bind(uri: Uri?, listener: PolarInitListener?) {
        Logger.d(TAG, "bind: uri=$uri")
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            mLastListener = listener
            handleOpeningURL(uri)
        }
    }

    override fun reBind(uri: Uri?, listener: PolarInitListener?) {
        Logger.d(TAG, "reBind: uri=$uri")
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            mLastListener = listener
            handleOpeningURL(uri)
        }
    }

    /**
     * Set userID and attributes:
     * - Create current user session if needed
     * - Backup user session into the otherUserSessions to keep running for sending events
     */
    override fun updateUser(userID: String?, attributes: Map<String, Any>?) {
        currentUserSession?.let { userSession ->
            if (userSession.userID != userID) {
                currentUserSession = null
                otherUserSessions.add(userSession)
            }
        }

        var events = mutableListOf<UntrackedEvent>()
        if (currentUserSession == null && userID != null) {
            val name = "events_${Date().time}_${UUID.randomUUID()}.json"
            val file = appDirectory.file(name)
            Logger.d(TAG, "TrackingEvents stored in `${file.absolutePath}`")

            events = pendingEvents
            pendingEvents = arrayListOf()

            currentUserSession = UserSession(
                organizationUnid = appId,
                userID = userID,
                trackingFileStorage = file
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            listOf(
                async { currentUserSession?.trackEvents(events) },
                async { currentUserSession?.setAttributes(attributes ?: emptyMap()) }
            )
        }
    }

    override fun trackEvent(name: String, attributes: Map<String, Any>) {
        CoroutineScope(Dispatchers.Main).launch {
            val date = DateTimeUtils.dateToString(
                source = Date(),
                format = Constants.DateTime.DEFAULT_DATE_FORMAT,
                timeZone = Constants.DateTime.utcTimeZone,
            )
            val userSession = currentUserSession
            if (userSession != null) {
                val events = listOf(UntrackedEvent(name, date, attributes))
                userSession.trackEvents(events)
            } else {
                if (pendingEvents.size == maxCapacity) {
                    pendingEvents.removeAt(0)
                }
                pendingEvents.add(UntrackedEvent(name, date, attributes))
            }
        }
    }

    private fun startTrackingAppLifeCycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {

            }

            override fun onStart(owner: LifecycleOwner) {
                trackEvent(
                    name = TrackEventModel.Type.APP_OPEN,
                    attributes = mapOf()
                )
            }

            override fun onResume(owner: LifecycleOwner) {
                trackEvent(
                    name = TrackEventModel.Type.APP_ACTIVE,
                    attributes = mapOf()
                )
            }

            override fun onPause(owner: LifecycleOwner) {
                trackEvent(
                    name = TrackEventModel.Type.APP_INACTIVE,
                    attributes = mapOf()
                )
            }

            override fun onStop(owner: LifecycleOwner) {
                trackEvent(
                    name = TrackEventModel.Type.APP_CLOSE,
                    attributes = mapOf()
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                trackEvent(
                    name = TrackEventModel.Type.APP_TERMINATE,
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

    private suspend fun handleOpeningURL(uri: Uri?) {
        val supportedBaseDomains = Configuration.Env.supportedBaseDomains
        val domain = uri?.host
        if (domain?.endsWith(supportedBaseDomains) != true) {
            mLastListener?.onInitFinished(null, null)
            return
        }
        val subDomain = domain.replace(supportedBaseDomains, "")
        val slug = uri.path?.replace("/", "") ?: ""
        val context = application.applicationContext
        val isFirstTimeLaunch = apiService.isFirstTimeLaunch(
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
                mLastLink = apiService.getLinkData(domain = subDomain, slug = slug)

                val trackRequest = TrackLinkClickRequest(
                    domain = subDomain,
                    slug = slug,
                    trackType = TrackLinkClickRequest.TrackType.APP_CLICK,
                    clickTime = clickTime,
                    fingerprint = TrackLinkClickRequest.Fingerprint.ANDROID_SDK,
                    deviceData = mapOf(),
                    additionalData = mapOf(),
                )
                val clid = uri.getQueryParameter("__clid")

                if (clid.isNullOrEmpty()) {
                    val linkClick = apiService.trackLinkClick(trackRequest)
                    val clickUnid = linkClick?.unid
                    val request = UpdateLinkClickRequest(sdkUsed = true)
                    apiService.updateLinkClick(clickUnid, request)
                } else {
                    val request = UpdateLinkClickRequest(sdkUsed = true)
                    apiService.updateLinkClick(clid, request)
                }
                onLinkClickHandler(uri.toString(), mLastLink?.data?.content, null)
                mLastListener?.onInitFinished(mLastLink?.data?.content, null)
            } catch (e: Exception) {
                onLinkClickHandler(uri.toString(), null, e)
                mLastListener?.onInitFinished(null, e)
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

open class PolarApp {
    open var apiKey = "invalid"

    open fun bind(uri: Uri?, listener: PolarInitListener?) {}
    open fun reBind(uri: Uri?, listener: PolarInitListener?) {}
    open fun updateUser(userID: String?, attributes: Map<String, Any>?) {}
    open fun trackEvent(name: String, attributes: Map<String, Any>) {}

    companion object {
        const val TAG = ">>>Polar"

        var isLoggingEnabled = false

        @Volatile
        private var _shared: PolarApp? = null

        val shared: PolarApp
            get() = _shared ?: synchronized(this) { // Ensure thread-safe
                _shared ?: run {
                    Logger.e(TAG, "Polar App hasn't initialized!")
                    PolarApp()
                }
            }

        fun initialize(
            appId: String,
            apiKey: String,
            onLinkClickHandler: OnLinkClickHandler
        ) {
            _shared = InternalPolarApp(
                appId = appId,
                apiKey = apiKey,
                onLinkClickHandler = onLinkClickHandler
            )

            val jsonString = """
            {
                "name": "John",
                "age": 30,
                "isAdmin": true,
                "scores": [100, 95, 90],
                "address": {
                    "city": "New York",
                    "zipcode": "10001"
                }
            }
            """

//            val model = MapModel.fromJson(jsonString)
//            Log.d("TESTING", "initialize: ${model.content}")
        }
    }
}
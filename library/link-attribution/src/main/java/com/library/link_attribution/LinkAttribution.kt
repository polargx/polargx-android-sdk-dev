package com.library.link_attribution

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.window.layout.WindowMetricsCalculator
import com.library.link_attribution.di.linkAttributeModule
import com.library.link_attribution.extension.getDeviceModel
import com.library.link_attribution.extension.getDeviceName
import com.library.link_attribution.extension.getIP4Address
import com.library.link_attribution.extension.getIP6Address
import com.library.link_attribution.extension.getManufacturer
import com.library.link_attribution.extension.getOsVersion
import com.library.link_attribution.extension.getSdkVersion
import com.library.link_attribution.listener.LinkInitListener
import com.library.link_attribution.repository.event.EventRepository
import com.library.link_attribution.repository.event.model.EventModel
import com.library.link_attribution.repository.event.remote.api.EventTrackRequest
import com.library.link_attribution.repository.link.LinkRepository
import com.library.link_attribution.repository.link.model.link.LinkDataModel
import com.library.link_attribution.repository.link.remote.api.click.LinkClickRequest
import com.library.link_attribution.repository.link.remote.api.track.LinkTrackRequest
import com.library.link_attribution.utils.DateTimeUtils
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Calendar
import java.util.TimeZone

class LinkAttribution(
    private val context: Context,
    private val appId: String?,
) : KoinComponent {

    private val eventRepository: EventRepository by inject()
    private val linkRepository: LinkRepository by inject()

    private lateinit var mKronosClock: KronosClock

    private var isAppInitialed: Boolean? = null

    private var mInitAppJob: Job? = null
    private var mEventTrackingJob: Job? = null
    private var mGetLinkJob: Job? = null

    private var mUri: Uri? = null
    private var isReInitializing: Boolean? = null

    private var mLastLink: LinkDataModel? = null

    private var mListener: LinkInitListener? = null

    companion object {
        const val TAG = ">>>LinkAttribution"
        const val ENDPOINT = "jw4xix6q44.execute-api.us-east-1.amazonaws.com/dev"
        const val X_API_KEY = "BFH3j4Gsgy4Blnh87SDmj3163J1Ska9139tTI7Wv"

        @SuppressLint("StaticFieldLeak")
        private var instance: LinkAttribution? = null

        fun initApp(
            context: Context,
            appId: String?,
        ) {
            if (instance == null) {
                instance = LinkAttribution(
                    context = context,
                    appId = appId,
                ).apply {
                    startInject()
                    mKronosClock = AndroidClockFactory.createKronosClock(context)
                    mKronosClock.syncInBackground()
                }
            }
            instance?.startInitializingApp()
        }

        fun init(
            activity: Activity,
            uri: Uri?,
            listener: LinkInitListener
        ) {
            instance?.mListener = listener
            instance?.init(activity = activity, uri = uri)
        }

        fun reInit(
            activity: Activity,
            uri: Uri?,
            listener: LinkInitListener
        ) {
            instance?.mListener = listener
            instance?.reInit(activity = activity, uri = uri)
        }
    }

    fun isKoinStarted(): Boolean {
        return GlobalContext.getOrNull() != null
    }

    private fun startInject() {
        if (isKoinStarted()) {
            loadKoinModules(linkAttributeModule)
            return
        }
        startKoin {
            androidLogger()
            androidContext(context)
            modules(linkAttributeModule)
        }
    }

    fun isAppInitializing(): Boolean {
        return mInitAppJob?.isActive == true
    }

    fun startInitializingApp() {
        if (mInitAppJob?.isActive == true) return
        mInitAppJob = CoroutineScope(Dispatchers.IO).launch {
            reset()
            var shouldRetry = true
            do {
                try {
                    val now = Calendar.getInstance().apply {
                        timeInMillis = mKronosClock.getCurrentTimeMs()
                    }
                    val launchEvent = EventModel(
                        organizationUnid = appId,
                        eventName = EventModel.Type.APP_LAUNCH,
                        eventTime = DateTimeUtils.calendarToString(
                            source = now,
                            format = LinkAttributionConstants.DateTime.DEFAULT_DATE_FORMAT,
                            timeZone = LinkAttributionConstants.DateTime.utcTimeZone,
                        ),
                        data = mutableMapOf()
                    )
                    val request = EventTrackRequest.from(launchEvent)
                    val response = eventRepository.rawTrack(request)
                    if (response.status.isSuccess()) {
                        Log.d(TAG, "startInitializingApp: successful ✅")
                        isAppInitialed = true
                        shouldRetry = false
                    }
                    if (response.status.value == 403) {
                        Log.d(TAG, "startInitializingApp: ⛔⛔⛔ INVALID appId or xApiKey! ⛔⛔⛔")
                        shouldRetry = false
                    }
                } catch (throwable: Throwable) {
                    when (throwable) {
                        is ConnectException -> {
                            // Handle connection refused or other connection issues (no internet)
                            Log.d(
                                TAG,
                                "startInitializingApp: ⛔No internet connection + retry 🔁 ex=$throwable"
                            )
                            shouldRetry = true
                            delay(1000)
                        }

                        is UnknownHostException -> {
                            // Handle DNS resolution failures (no internet or incorrect URL)
                            Log.d(
                                TAG,
                                "startInitializingApp: ⛔Unknown host + retry 🔁 ex=$throwable"
                            )
                            shouldRetry = true
                            delay(1000)
                        }

                        else -> {
                            // Handle other exceptions (e.g., server errors, JSON parsing)
                            Log.d(
                                TAG,
                                "startInitializingApp: ⛔⛔⛔An error occurred ⛔⛔⛔ ex=$throwable"
                            )
                            shouldRetry = false
                        }
                    }
                }
            } while (shouldRetry)
        }
    }

    private suspend fun reset() {
        eventRepository.reset()
        linkRepository.reset()
    }

    fun trackEvent(
        @EventModel.Type type: String?,
        data: Map<String, String?>?
    ) {
        trackEvent(
            type = type,
            time = Calendar.getInstance().apply {
                timeInMillis = mKronosClock.getCurrentTimeMs()
            },
            data = data
        )
    }

    fun trackEvent(
        @EventModel.Type type: String?,
        time: Calendar,
        data: Map<String, String?>?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val event = EventModel(
                organizationUnid = appId,
                eventName = type,
                eventTime = DateTimeUtils.calendarToString(
                    source = time,
                    format = LinkAttributionConstants.DateTime.DEFAULT_DATE_FORMAT,
                ),
                data = data
            )
            val eventList = eventRepository.getCacheEventList()?.toMutableList() ?: mutableListOf()
            eventList.add(event)
            eventRepository.setCacheEventList(eventList)
            startTrackingQueueIfNeeded()
        }
    }

    private fun startTrackingQueueIfNeeded() {
        if (mEventTrackingJob?.isActive == true) return
        mEventTrackingJob = CoroutineScope(Dispatchers.IO).launch {
            eventRepository.getCacheEventList()?.let { eventList ->
                if (eventList.isEmpty()) return@launch
                eventList.toMutableList().map { event ->
                    async {
                        val request = EventTrackRequest.from(event)
                        val response = eventRepository.rawTrack(request)
                        if (response.status.isSuccess()) {
                            Log.d(TAG, "startTrackingQueueIfNeeded: successful ✅, event=$event")
                            val latestEventList =
                                eventRepository.getCacheEventList()?.toMutableList()
                            latestEventList?.remove(event)
                            eventRepository.setCacheEventList(latestEventList)
                        }
                    }
                }.awaitAll()

                startTrackingQueueIfNeeded()
            }
        }
    }

    fun init(activity: Activity?, uri: Uri?) {
        isReInitializing = false
        mUri = uri ?: activity?.intent?.data
        handleFetchLinkData(activity = activity)
    }

    fun reInit(activity: Activity?, uri: Uri?) {
        isReInitializing = true
        mUri = uri ?: activity?.intent?.data
        handleFetchLinkData(activity = activity)
    }

    private fun handleFetchLinkData(activity: Activity?) {
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            val domain = mUri?.host
            if (domain?.endsWith(LinkAttributionConstants.Configuration.DOMAIN_SUFFIX) != true) {
                Log.d(TAG, "handleFetchLinkData: Invalid domain! domain=$domain")
                return@launch
            }
            val subDomain = domain.replace(LinkAttributionConstants.Configuration.DOMAIN_SUFFIX, "")
            val path = mUri?.path?.replace("/", "")
            val isFirstTimeLaunch = eventRepository.isFirstTimeLaunch(
                activity,
                mKronosClock.getCurrentTimeMs()
            )
            val clickTime = Calendar.getInstance().apply {
                timeInMillis = mKronosClock.getCurrentTimeMs()
            }
            if (!mUri?.path.isNullOrEmpty()) {
                try {
                    val getLinkResponse = linkRepository.fetchLinkData(
                        domain = subDomain,
                        slug = path
                    )
                    mLastLink = getLinkResponse.data?.sdkLinkData?.toExternal()

                    val trackRequest = LinkTrackRequest(
                        clickTime = DateTimeUtils.calendarToString(
                            clickTime,
                            LinkAttributionConstants.DateTime.DEFAULT_DATE_FORMAT,
                            LinkAttributionConstants.DateTime.utcTimeZone,
                        ),
                        domain = subDomain,
                        slug = path,
                        fingerprint = LinkTrackRequest.Fingerprint.ANDROID_SDK,
                        trackType = LinkTrackRequest.TrackType.APP_CLICK,
                        deviceData = mutableMapOf(),
                        additionalData = mutableMapOf(),
                    )
                    val trackResponse = linkRepository.track(trackRequest)
                    val linkClickUnid = trackResponse.data?.linkClick?.unid
                    val request = LinkClickRequest(sdkUsed = true)
                    linkRepository.linkClick(linkClickUnid, request)
                    mListener?.onInitFinished(mLastLink?.data, null)
                } catch (throwable: Throwable) {
                    mListener?.onInitFinished(null, throwable)
                }
                return@launch
            }
            if (isFirstTimeLaunch && mUri?.path.isNullOrEmpty()) {
                val windowMetrics =
                    if (activity == null) null else WindowMetricsCalculator.getOrCreate()
                        .computeCurrentWindowMetrics(activity)
                val width = windowMetrics?.bounds?.width()
                val height = windowMetrics?.bounds?.height()

                // Get density using Resources
                val metrics = activity?.resources?.displayMetrics
                val density = metrics?.density
                val densityDpi = metrics?.densityDpi

                Log.d(
                    TAG, "initSession: " +
                            "\nIP4Address=${context.getIP4Address()}" +
                            "\nIP6Address=${context.getIP6Address()}" +
                            "\nosVersion=${context.getOsVersion()}" +
                            "\nsdkVersion=${context.getSdkVersion()}" +
                            "\ndeviceModel=${context.getDeviceModel()}" +
                            "\nmanufacturer=${context.getManufacturer()}" +
                            "\ndeviceName=${context.getDeviceName()}" +
                            "\nwindow.width=${width}" +
                            "\nwindow.height=${height}" +
                            "\nwindow.density=${density}" +
                            "\nwindow.densityDpi=${densityDpi}"
                )
//                linkRepository.fetchLinkMatches()
//                getLinkByMatching()
            }
        }
    }

    private fun onInternetConnectionChanged(connected: Boolean) {
        if (connected) {
            if (isAppInitialed == true) {
                startTrackingQueueIfNeeded()
                return
            }
            if (isAppInitializing()) return
            startInitializingApp()
        }
    }
}
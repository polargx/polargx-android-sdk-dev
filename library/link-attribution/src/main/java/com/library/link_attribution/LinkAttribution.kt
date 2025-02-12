package com.library.link_attribution

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.window.layout.WindowMetricsCalculator
import com.data.shared.ApiError
import com.library.link_attribution.di.linkAttributeModule
import com.library.link_attribution.extension.getDeviceModel
import com.library.link_attribution.extension.getDeviceName
import com.library.link_attribution.extension.getIP4Address
import com.library.link_attribution.extension.getIP6Address
import com.library.link_attribution.extension.getManufacturer
import com.library.link_attribution.extension.getOsVersion
import com.library.link_attribution.extension.getSdkVersion
import com.library.link_attribution.listener.LinkInitListener
import com.library.link_attribution.repository.configuration.ConfigurationRepository
import com.library.link_attribution.repository.configuration.model.InitSessionModel
import com.library.link_attribution.repository.configuration.remote.api.init.InitSessionRequest
import com.library.link_attribution.repository.link.LinkRepository
import com.library.link_attribution.repository.link.model.LinkModel
import com.library.link_attribution.repository.link.remote.api.matching.GetLinkByMatchingRequest
import com.library.link_attribution.repository.tracking.TrackingRepository
import com.library.link_attribution.repository.tracking.remote.api.click.TrackClickRequest
import com.library.link_attribution.repository.tracking.remote.api.event.TrackEventRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.append
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import java.io.IOException

class LinkAttribution(
    private val context: Context,
    private val appUnid: String?,
    private val apiKey: String?,
) : KoinComponent {

    //    private lateinit var configurationRepository: ConfigurationRepository
    private val configurationRepository: ConfigurationRepository by inject()
    private val linkRepository: LinkRepository by inject()
    private val trackingRepository: TrackingRepository by inject()

    private var mUri: Uri? = null
    private var isReInitializing: Boolean? = null
    private var mListener: LinkInitListener? = null

    private var mInitSession: InitSessionModel? = null

    //    private var mConfiguration: ConfigurationModel? = null
    private var mLastLink: LinkModel? = null

    private var mInitSessionJob: Job? = null
    private var mGetLinkJob: Job? = null

    companion object {
        const val TAG = ">>>LinkAttribution"
        const val ENDPOINT = "ec2-52-70-12-200.compute-1.amazonaws.com:1323"
        const val X_API_KEY = ""

        @SuppressLint("StaticFieldLeak")
        private var instance: LinkAttribution? = null

        fun initApp(
            context: Context,
            appUnid: String?,
            apiKey: String?,
        ): LinkAttribution {
            if (instance == null) {
                instance = LinkAttribution(
                    context = context,
                    appUnid = appUnid,
                    apiKey = apiKey
                )
            }
            instance?.injectManually()
            instance?.startInject()
            return instance ?: throw Exception("LinkAttributionApp hasn't been initialized!")
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

    fun startInitializingApp() {
        CoroutineScope(Dispatchers.Main).launch {
            var error: Exception?
            do {
                try {
                    trackEvent()
                    error = null
                    Log.d(TAG, "startInitializingApp: successful ✅")
                } catch (e: IOException) {
                    Log.d(TAG, "startInitializingApp: failed ⛔️ + retry 🔁 $e")
                    error = e
                    delay(1000)
                } catch (e: Exception) {
                    Log.d(TAG, "startInitializingApp: failed ⛔️ + stop ⛔️ $e")
                    error = null
                }
            } while (error != null)
        }
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

    private fun getClient(endpoint: String, xApiKey: String): HttpClient {
        val client = HttpClient(Android) {
            engine {
                socketTimeout = 60_000
                connectTimeout = 60_000
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTP
//                    protocol = URLProtocol.HTTPS
                    host = endpoint
                }
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
//                    append("x-api-key", xApiKey)
                }
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(0)
                exponentialDelay()
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.i("HttpClient", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpCache)
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
            }

            HttpResponseValidator {
                validateResponse { response ->
                    Log.d(TAG, "validateResponse: response=$response")
                    if (!response.status.isSuccess()) {
                        throw ClientRequestException(response, "")
                    }
                }

                handleResponseExceptionWithRequest { cause, request ->
                    Log.d(
                        TAG,
                        "handleResponseExceptionWithRequest: cause=$cause, request=$request"
                    )
                    try {
                        if (cause !is ClientRequestException) throw cause
                        val errorData = cause.response.bodyAsText()
                        val error = ApiError(errorData)
                        Log.d(
                            TAG,
                            "handleResponseExceptionWithRequest: error=${error}, errorData=${errorData}"
                        )
                        when (error.code) {

                        }
                        throw ApiError(errorData)
                    } catch (ex: Throwable) {
                        Log.d(TAG, "response: ex=${ex}")
                        throw ex
                    }
                }
            }

        }

        client.plugin(HttpSend).intercept { request ->
            Log.d(TAG, "request=$request")
            if (request.url.encodedPath.endsWith("users/password/login", true)
                || request.url.encodedPath.endsWith("users/password/signup", true)
                || request.url.encodedPath.endsWith("users/anon/signup", true)
                || request.url.encodedPath.endsWith("users/password/forgot", true)
            ) {
                request.headers.remove("token")
            }
            execute(request)
        }
        return client
    }

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("linkAttribution.file", Context.MODE_PRIVATE)
    }

    private fun injectManually() {
        val sf = getSharedPreferences()
        val httpClient = getClient(endpoint = ENDPOINT, xApiKey = X_API_KEY)
//        configurationRepository = ConfigurationRepositoryImpl(
//            localDatasource = ConfigurationLocalDatasourceImpl(sf = sf),
//            remoteDatasource = ConfigurationRemoteDatasourceImpl(client = httpClient)
//        )
//        linkRepository = LinkRepositoryImpl(
//            localDatasource = LinkLocalDatasourceImpl(sf = sf),
//            remoteDatasource = LinkRemoteDatasourceImpl(client = httpClient)
//        )
//        trackingRepository = TrackingRepositoryImpl(
//            localDatasource = TrackingLocalDatasourceImpl(sf = sf),
//            remoteDatasource = TrackingRemoteDatasourceImpl(client = httpClient)
//        )
    }

    fun init(activity: Activity?, uri: Uri?) {
        isReInitializing = false
        mUri = uri ?: activity?.intent?.data
        if (mInitSessionJob?.isActive == true) return
        handleInitSession(activity = activity)
    }

    fun reInit(activity: Activity?, uri: Uri?) {
        isReInitializing = true
        mUri = uri ?: activity?.intent?.data
        if (mInitSessionJob?.isActive == true) return
        handleInitSession(activity = activity)
    }

    private fun handleInitSession(activity: Activity?) {
        val windowMetrics = if (activity == null) null else WindowMetricsCalculator.getOrCreate()
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
        if (mInitSessionJob?.isActive == true) {
            mInitSessionJob?.cancel()
        }
        mInitSessionJob = CoroutineScope(Dispatchers.IO).launch {
            val request = InitSessionRequest()
            configurationRepository.initSession(
                appUnid = appUnid,
                apiKey = apiKey,
                request = request
            ).flowOn(Dispatchers.IO)
                .onStart { Log.d(TAG, "initSession:onStart") }
                .catch { error ->
                    Log.d(TAG, "initSession:catch: error=$error")
                    if (mUri?.path.isNullOrEmpty()) {
                        getLinkByMatching()
                    } else {
                        getLinkByPath(mUri?.path?.replace("/", ""))
                    }
                }
                .onCompletion { Log.d(TAG, "initSession:onCompletion") }
                .collect { initSession ->
                    Log.d(TAG, "initSession:collect: initSession=$initSession")
                    mInitSession = initSession
                    if (mUri?.path.isNullOrEmpty()) {
                        getLinkByMatching()
                    } else {
                        getLinkByPath(mUri?.path?.replace("/", ""))
                    }
                }
        }
    }

    private fun getLinkByPath(path: String?) {
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            linkRepository.fetchAndCacheLinkByPath(
                appUnid = appUnid,
                apiKey = apiKey,
                path = path
            )
                .flowOn(Dispatchers.IO)
                .onStart { Log.d(TAG, "getLinkByPath:onStart") }
                .catch { error ->
                    Log.d(TAG, "getLinkByPath:catch: error=$error")
                    mListener?.onInitFinished(null, error)
                }
                .onCompletion { Log.d(TAG, "getLinkByPath:onCompletion") }
                .collect { link ->
                    Log.d(TAG, "getLinkByPath:collect: link=$link")
                    mLastLink = link
                    mListener?.onInitFinished(mLastLink?.attributes, null)
                    trackClick(link)
                }
        }
    }

    private fun getLinkByMatching() {
        if (mGetLinkJob?.isActive == true) {
            mGetLinkJob?.cancel()
        }
        mGetLinkJob = CoroutineScope(Dispatchers.IO).launch {
            val request = GetLinkByMatchingRequest()
            linkRepository.fetchAndCacheLinkByMatching(
                appUnid = appUnid,
                apiKey = apiKey,
                request = request
            )
                .flowOn(Dispatchers.IO)
                .onStart { Log.d(TAG, "getLinkByMatching:onStart") }
                .catch { error ->
                    Log.d(TAG, "getLinkByMatching:catch: error=$error")
                    mListener?.onInitFinished(null, error)
                }
                .onCompletion { Log.d(TAG, "getLinkByMatching:onCompletion") }
                .collect { link ->
                    Log.d(TAG, "getLinkByMatching:collect: link=$link")
                    mLastLink = link
                    mListener?.onInitFinished(mLastLink?.attributes, null)
                    trackClick(link)
                }
        }
    }

    private fun trackClick(link: LinkModel?) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = TrackClickRequest(
                path = link?.path
            )
            trackingRepository.trackClick(
                appUnid = appUnid,
                apiKey = apiKey,
                request = request
            )
                .flowOn(Dispatchers.IO)
                .onStart { Log.d(TAG, "trackClick:onStart") }
                .catch { error ->
                    Log.d(TAG, "trackClick:catch: error=$error")
                }
                .onCompletion { Log.d(TAG, "trackClick:onCompletion") }
                .collect { link ->
                    Log.d(TAG, "trackClick:collect: link=$link")
                }
        }
    }

    private fun trackEvent() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = TrackEventRequest(
                organizationUnid = appUnid,
                eventName = "app_launch",
                data = TrackEventRequest.Data()
            )
            trackingRepository.trackEvent(request)
                .flowOn(Dispatchers.IO)
                .onStart { Log.d(TAG, "trackEvent: onStart") }
                .onCompletion { Log.d(TAG, "trackEvent: onCompletion") }
                .catch { error -> Log.d(TAG, "trackEvent: catch: error=$error") }
                .collect { Log.d(TAG, "trackEvent: collect") }
        }
    }
}
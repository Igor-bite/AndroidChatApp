package com.klyuzhevigor.ChatApp.Services

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import com.klyuzhevigor.ChatApp.Auth.Authorization
import com.klyuzhevigor.ChatApp.Auth.TokenStorage
import com.klyuzhevigor.ChatApp.database.AppDatabase
import com.klyuzhevigor.ChatApp.database.DBChatsRepository
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException;
import javax.net.ssl.TrustManagerFactory

class DefaultAppContainer {
    private lateinit var applicationContext: Context
    lateinit var tokenStorage: TokenStorage

    fun init(context: Context) {
        applicationContext = context
        tokenStorage = TokenStorage(context)
        tokenStorage.getToken()?.let {
            auth.token = it
        }
    }

    private val baseUrl = "https://faerytea.name:8008"

    private val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .client(getUnsafeOkHttpClient(null))
            .build()

    private val authorizedRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .client(getUnsafeOkHttpClient(auth.token))
            .build()
    }

    private fun getUnsafeOkHttpClient(authToken: String?): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                        return arrayOf()
                    }
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager

            val builder = OkHttpClient.Builder()
            authToken?.let {
                val int = AuthTokenInterceptor()
                int.useToken(it)
                builder.addInterceptor(int)
            }
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private val chatsDataProvider: ChatsDataProvider by lazy {
        authorizedRetrofit.create(ChatsDataProvider::class.java)
    }

    private val networkChatsRepo: NetworkChatsRepository by lazy {
        NetworkChatsRepository(chatsDataProvider)
    }

    val auth: AuthManager by lazy {
        AuthManager(authApiService) {
            tokenStorage.saveToken(it)
        }
    }

    private val authApiService: Authorization by lazy {
        retrofit.create(Authorization::class.java)
    }

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db")
            .build()
    }

    private val dbChatsRepository: DBChatsRepository by lazy { DBChatsRepository(appDatabase.getChatsDao()) }

    val chatsRepo: ChatsRepository by lazy { MainChatsRepository(networkChatsRepo, dbChatsRepository, connectivityManager) }

    private val connectivityManager: ConnectivityManager by lazy { applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
}

class AuthTokenInterceptor : Interceptor {
    private var token = ""

    fun useToken(token: String ) {
        this.token = token;
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (token.isNotEmpty()) {
            request = request.newBuilder()
                .addHeader("X-Auth-Token", token)
                .build()
        }
        return chain.proceed(request)
    }
}
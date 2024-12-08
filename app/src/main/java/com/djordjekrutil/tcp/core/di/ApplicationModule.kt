package com.djordjekrutil.tcp.core.di

import android.annotation.SuppressLint
import android.content.Context
import com.djordjekrutil.tcp.BuildConfig
import com.djordjekrutil.tcp.core.platform.NetworkHandler
import com.djordjekrutil.tcp.feature.db.AppDatabase
import com.djordjekrutil.tcp.feature.repository.TasksRepository
import com.djordjekrutil.tcp.feature.service.TasksService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule() {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.URL_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        okHttpClientBuilder.hostnameVerifier { _, _ -> true }
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)


    @Provides
    @Singleton
    fun provideTasksNetwork(
        networkHandler: NetworkHandler,
        tasksService: TasksService
    ): TasksRepository.Network {
        return TasksRepository.Network(networkHandler, tasksService)
    }

    @Provides
    @Singleton
    fun provideTasksRepository(
        network: TasksRepository.Network,
        database: TasksRepository.Database
    ): TasksRepository {
        return TasksRepository.TasksRepositoryImpl(network, database)
    }
}
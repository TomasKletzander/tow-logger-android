package cz.dmn.towlogger.di

import com.google.gson.Gson
import cz.dmn.towlogger.core.io.api.ApiAdapter
import cz.dmn.towlogger.core.io.api.lkcm.LkcmApi
import cz.dmn.towlogger.core.io.api.lkcm.LkcmApiAdapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    companion object {
        val CONN_TIMEOUT_SECONDS = 20L
        val READ_TIMEOUT_SECONDS = 20L
        val WRITE_TIMEOUT_SECONDS = 20L
        val LKCM_API_BASE_URL = "http://api.dmn.cz/lkcm/"

        private fun provideBaseOkHttpClientBuilder(): OkHttpClient.Builder {
            val builder = OkHttpClient.Builder()
                    .connectTimeout(CONN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            return builder
        }

        private fun buildClient(baseUrl: String, httpClient: OkHttpClient, gson: Gson) = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideApiAdapter(adapter: LkcmApiAdapter): ApiAdapter = adapter

    @Provides
    @Singleton
    fun provideLkcmApi(client: OkHttpClient, gson: Gson): LkcmApi = buildClient(LKCM_API_BASE_URL, client, gson).create(LkcmApi::class.java)

    @Provides
    fun provideOkHttpClient() = provideBaseOkHttpClientBuilder().build()
}

package com.ipsoft.bibliasagrada.di

import com.ipsoft.bibliasagrada.BuildConfig
import com.ipsoft.bibliasagrada.data.remote.bible.ChurchRoomApi
import com.ipsoft.bibliasagrada.domain.common.constants.BIBLE_BASE_URL
import com.ipsoft.bibliasagrada.domain.common.constants.SOCKET_TIMEOUT
import com.ipsoft.bibliasagrada.domain.core.plataform.HeaderInterceptor
import com.ipsoft.bibliasagrada.domain.repository.BibleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BIBLE_BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun createBibleService(retrofit: Retrofit): ChurchRoomApi {
        return retrofit.create(ChurchRoomApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBibleRepository(dataSource: BibleRepository.Network): BibleRepository =
        dataSource

    private fun createClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        val headerInterceptor = HeaderInterceptor()
        okHttpClientBuilder.addInterceptor(headerInterceptor)
        okHttpClientBuilder.connectTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
        okHttpClientBuilder.readTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)

        return okHttpClientBuilder.build()
    }
}

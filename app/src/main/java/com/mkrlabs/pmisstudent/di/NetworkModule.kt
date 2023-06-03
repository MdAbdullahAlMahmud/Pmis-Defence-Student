package com.mkrlabs.pmisstudent.di

import com.mkrlabs.pmisstudent.api.AppAPI
import com.mkrlabs.pmisstudent.util.Constant
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
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiInterface(): AppAPI {
        val retrofit = getRetrofit(okHttpClient)
        return retrofit.create(AppAPI::class.java)
    }

    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @get:Provides
    @Singleton
    val okHttpClient: OkHttpClient
        get() {
            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.connectTimeout(Constant.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            okHttpClientBuilder.readTimeout(Constant.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            okHttpClientBuilder.writeTimeout(Constant.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
            okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            return okHttpClientBuilder.build()
        }



}
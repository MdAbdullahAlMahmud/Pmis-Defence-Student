package com.mkrlabs.pmisstudent.api

import com.mkrlabs.pmisstudent.util.Constant.BASE_URL

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object{



        private  val  retrofit by  lazy {
            val logging = HttpLoggingInterceptor()

            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val  client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

/*
            val client =OkHttpClient. Builder().addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response? {
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    return chain.proceed(newRequest)
                }
            }).build()
*/
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }




        val api by lazy {

            retrofit.create(AppAPI::class.java)
        }
    }
}
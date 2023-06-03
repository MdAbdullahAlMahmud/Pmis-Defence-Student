package com.mkrlabs.pmisstudent.api

import com.mkrlabs.pmisstudent.model.NotificationItem
import com.mkrlabs.pmisstudent.model.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AppAPI{

    @POST("send")
    suspend fun postNotification(@Body() notificationItem: NotificationItem, @Header("Authorization") authHeader: String?) : Response<NotificationResponse>


}
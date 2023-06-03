package com.mkrlabs.pmisstudent.model

data class NotificationItem(


    val  to : String ,
    val notification: NotificationBody,
    val data : MetaData
)


data class NotificationBody(
    val title : String,
    val body : String,
)


data class MetaData(
    val  project_id : String
)

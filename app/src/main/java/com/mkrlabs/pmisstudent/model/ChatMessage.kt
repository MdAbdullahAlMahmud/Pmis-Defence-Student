package com.mkrlabs.pmisstudent.model

data class ChatMessage(
    var  message : String = "",
    var messageId : String = "",
    var senderId  : String= "",
    var  timestamp : Long=0L,
    var senderName : String ="",
    var type : MessageType = MessageType.TEXT
)

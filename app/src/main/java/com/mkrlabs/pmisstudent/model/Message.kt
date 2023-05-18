package com.mkrlabs.pmisstudent.model

/*var message :String ,
var messageType: MessageType,
val layoutType: LayoutType,
val timestamp: Long,
val image :String*/

 class Message{
     var message :String =""
     var messageId :String =""
     var senderId :String =""
     var messageType: MessageType? = null
     var layoutType: LayoutType? =null
     var timestamp: Long = 0L
     var image :String = ""
     var senderName :String? =null


     constructor()

     constructor(senderId : String, senderName:String , message : String,messageId :String, messageType: MessageType,image :String,timestamp : Long){
         this.senderId = senderId
         this.senderName = senderName
         this.message = message
         this.messageId = messageId
         this.messageType = messageType
         this.image = image
         this.timestamp =timestamp
     }
     constructor(senderId : String, senderName:String , message : String,messageId :String, messageType: MessageType,image :String,timestamp : Long, layoutType: LayoutType){
         this.senderId = senderId
         this.senderName = senderName
         this.message = message
         this.messageId = messageId
         this.messageType = messageType
         this.image = image
         this.timestamp =timestamp
         this.layoutType = layoutType
     }


 }

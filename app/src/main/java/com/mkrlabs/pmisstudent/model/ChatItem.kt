package com.mkrlabs.pmisstudent.model

import java.io.Serializable
import java.sql.Timestamp

data class ChatItem (
   var name : String = "",
   var uid : String = "",
   var designation : String = "",
   var timestamp: Long = 0L,
   var addedBy : String = "",
   var image : String = "",
   var chatTYPE: ChatTYPE = ChatTYPE.NORMAL
) : Serializable {
}

enum class ChatTYPE{
   NORMAL,GROUP
}
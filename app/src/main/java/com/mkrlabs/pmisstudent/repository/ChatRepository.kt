package com.mkrlabs.pmisstudent.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mkrlabs.pmisstudent.model.ChatItem
import com.mkrlabs.pmisstudent.model.ChatMessage
import com.mkrlabs.pmisstudent.model.ChatTYPE
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Constant
import com.mkrlabs.pmisstudent.util.Resource
import javax.inject.Inject

class ChatRepository @Inject constructor( val firebaseFirestore: FirebaseFirestore, val mAuth: FirebaseAuth
){
    suspend fun chatUserList(projectId : String , result: (Resource<List<ChatItem>>) -> Unit){
        val userList = arrayListOf<ChatItem>()


        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TEAM_MEMBER_NODE)
            .whereNotEqualTo("uid",CommonFunction.loggedInUserUID())
            .get().addOnSuccessListener {
                for (document in it) {
                    val user = document.toObject(ChatItem::class.java)
                    user.chatTYPE = ChatTYPE.NORMAL
                    userList.add(user)
                }

            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }

        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.GROUP_NODE)
            .whereNotEqualTo("uid",CommonFunction.loggedInUserUID())

            .get().addOnSuccessListener {
                for (document in it) {
                    val user = document.toObject(ChatItem::class.java)
                    user.chatTYPE=ChatTYPE.GROUP
                    userList.add(user)
                }
                result.invoke(Resource.Success(userList))

            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }





    }

    suspend fun  sendMessage(mineChatUID : String , hisChatUID:String ,message: ChatMessage, result: (Resource<ChatMessage>) -> Unit){

        val uniqueMessageId = firebaseFirestore.collection(Constant.CHAT_NODE).document().id
        message.messageId = uniqueMessageId
        firebaseFirestore.collection(Constant.CHAT_NODE)
            .document(mineChatUID)
            .collection(Constant.MESSAGE_NODE)
            .document(uniqueMessageId)
            .set(message)
            .addOnSuccessListener {
                firebaseFirestore.collection(Constant.CHAT_NODE)
                    .document(hisChatUID)
                    .collection(Constant.MESSAGE_NODE)
                    .document(uniqueMessageId)
                    .set(message).addOnSuccessListener {
                        result.invoke(Resource.Success(message))
                    }.addOnFailureListener {
                        result.invoke(Resource.Error(it.localizedMessage.toString()))
                    }

            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }

    }

    suspend fun messageList(mineChatUID: String, result: (Resource<List<ChatMessage>>) -> Unit){


        firebaseFirestore.collection(Constant.CHAT_NODE)
            .document(mineChatUID)
            .collection(Constant.MESSAGE_NODE)
            .get()
            .addOnSuccessListener {
                val messageList = arrayListOf<ChatMessage>()
                for (document in it) {
                    val message = document.toObject(ChatMessage::class.java)
                    messageList.add(message)
                }
                result.invoke(
                    Resource.Success(messageList)
                )
            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }



    }
}
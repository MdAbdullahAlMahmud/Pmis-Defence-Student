package com.mkrlabs.pmisstudent.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mkrlabs.pmisstudent.model.ChatItem
import com.mkrlabs.pmisstudent.model.ChatMessage
import com.mkrlabs.pmisstudent.repository.ChatRepository
import com.mkrlabs.pmisstudent.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(val repository: ChatRepository , val mAuth : FirebaseAuth ):ViewModel(){


    var sendMessageState : MutableLiveData<Resource<ChatMessage>> = MutableLiveData()
    var mineMessageState : MutableLiveData<Resource<List<ChatMessage>>> = MutableLiveData()
    var chatUserListState : MutableLiveData<Resource<List<ChatItem>>> = MutableLiveData()



    fun sendMessage( hisUID : String , chatMessage: ChatMessage){
        val mineUID = mAuth.currentUser?.uid
        val  mineChatUID = "${mineUID}_${hisUID}"
        val  hisChatUID = "${hisUID}_${mineUID}"

        sendMessageState.postValue(Resource.Loading())
        viewModelScope.launch{
            repository.sendMessage(mineChatUID,hisChatUID,chatMessage){
                sendMessageState.postValue(it)
            }
        }
    }



    fun messageListMine(hisUID : String){
     val mineUID = mAuth.currentUser?.uid
     val  mineChatUID = "${mineUID}_${hisUID}"

        mineMessageState.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.messageList(mineChatUID){
                mineMessageState.postValue(it)
            }
        }
    }

    fun  chatUserList(projectId : String){
        chatUserListState.postValue(Resource.Loading())

        viewModelScope.launch {
            repository.chatUserList(projectId) {

                chatUserListState.postValue(it)
            }
        }
    }




}
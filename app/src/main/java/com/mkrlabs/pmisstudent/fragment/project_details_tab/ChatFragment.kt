package com.mkrlabs.pmisstudent.fragment.project_details_tab

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mkrlabs.pmisstudent.adapter.MessageAdapter
import com.mkrlabs.pmisstudent.databinding.FragmentChatBinding
import com.mkrlabs.pmisstudent.model.ChatTYPE
import com.mkrlabs.pmisstudent.model.LayoutType
import com.mkrlabs.pmisstudent.model.Message
import com.mkrlabs.pmisstudent.model.MessageType
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Constant
import com.mkrlabs.pmisstudent.util.Constant.MESSAGE_NODE
import com.mkrlabs.pmisstudent.util.SharedPref
import com.mkrlabs.pmisstudent.view_model.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding

    lateinit var messageAdapter :MessageAdapter

    lateinit var chatMessageList : ArrayList<Message>

    lateinit var database : DatabaseReference

    val  chatItem : ChatFragmentArgs by navArgs()
     var CHAT_ROOM_MINE :String = ""
     var CHAT_ROOM_HIS :String = ""
     var CHAT_ROOM_GROUP :String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        database = FirebaseDatabase.getInstance().reference
        chatMessageList = ArrayList()






        messageAdapter = MessageAdapter(chatMessageList,chatItem.chatItem.image)
        initRecycleView(view.context)


        binding.sendButton.setOnClickListener {


            if (binding.messageInput.text.toString().isEmpty()){

                Toast.makeText(view.context,"Please write something !!", Toast.LENGTH_SHORT).show()
            }else{

                var messageText = binding.messageInput.text.toString()
                val messageId = database.push().key.toString()

                var messageItem = Message(CommonFunction.loggedInUserUID(),"",messageText,messageId,MessageType.TEXT,"",Date().time)
                sendMessage(view.context,messageItem)
            }
        }


        val handler = Handler(Looper.myLooper()!!)

        binding.messageInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                database
                    .child(Constant.CHAT_NODE)
                    .child(CHAT_ROOM_HIS)
                    .child(Constant.TYPING_STATUS_KEY)
                    .setValue("typing..")

                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }

            var userStoppedTyping = Runnable {
                database
                    .child(Constant.CHAT_NODE)
                    .child(CHAT_ROOM_HIS)
                    .child(Constant.TYPING_STATUS_KEY)
                    .setValue("")
            }
        })

        fetchAllMessage(view.context)




        database.child(Constant.CHAT_NODE)
            .child(CHAT_ROOM_MINE)
            .child(Constant.TYPING_STATUS_KEY)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        Log.v("TypingStatus", "Exists  ${snapshot.value.toString()}")
                        binding.OnChatUserTypingStatus.text = snapshot.value.toString()
                    }else{
                        binding.OnChatUserTypingStatus.text = ""
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    CommonFunction.errorToast(view.context,"Error ${error.message}")
                }
            })


        binding.OnChatBackButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun fetchAllMessage(context: Context){
        if (chatItem.chatItem.chatTYPE==ChatTYPE.NORMAL){
            database.child(Constant.CHAT_NODE)
                .child(CHAT_ROOM_MINE)
                .child(MESSAGE_NODE)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatMessageList.clear()
                        for (data in snapshot.children){
                            var message : Message? = data.getValue(Message::class.java)
                            if (message != null) {
                                if (message.senderId.equals(CommonFunction.loggedInUserUID())){
                                    message.layoutType = LayoutType.SENDER
                                }else{
                                    message.layoutType = LayoutType.RECEIVER
                                }
                                chatMessageList.add(message)
                            }
                        }
                        messageAdapter.notifyDataSetChanged()
                        binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)

                    }
                    override fun onCancelled(error: DatabaseError) {
                        CommonFunction.errorToast(context,"Error ${error.message}")
                    }

                })

        }else{
            database.child(Constant.CHAT_NODE)
                .child(CHAT_ROOM_GROUP)
                .child(MESSAGE_NODE)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatMessageList.clear()
                        for (data in snapshot.children){
                            var message : Message? = data.getValue(Message::class.java)
                            if (message != null) {
                                if (message.senderId.equals(CommonFunction.loggedInUserUID())){
                                    message.layoutType = LayoutType.SENDER
                                }else{
                                    message.layoutType = LayoutType.RECEIVER
                                }
                                chatMessageList.add(message)
                            }
                        }
                        messageAdapter.notifyDataSetChanged()
                        binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)

                    }
                    override fun onCancelled(error: DatabaseError) {
                        CommonFunction.errorToast(context,"Error ${error.message}")
                    }

                })

        }
    }
    fun init(){
        buildChatRoom()
        setUpUserProfile()
    }
    fun setUpUserProfile(){

        binding.OnChatUserName.text = chatItem.chatItem.name.toString()
    }

    fun sendMessage(context: Context, message: Message){
        if (chatItem.chatItem.chatTYPE==ChatTYPE.NORMAL){
            database.child(Constant.CHAT_NODE)
                .child(CHAT_ROOM_MINE)
                .child(MESSAGE_NODE)
                .child(message.messageId)
                .setValue(message)
                .addOnSuccessListener {
                    database.child(Constant.CHAT_NODE)
                        .child(CHAT_ROOM_HIS)
                        .child(MESSAGE_NODE)
                        .child(message.messageId)
                        .setValue(message)
                        .addOnSuccessListener {

                            binding.messageInput.setText("")
                            binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)


                        }.addOnFailureListener {
                            CommonFunction.errorToast(context,"Message sent failed with error ${it.message}")
                            it.printStackTrace()
                        }

                }.addOnFailureListener {
                    CommonFunction.errorToast(context,"Message sent failed with error ${it.message}")
                    it.printStackTrace()
                }

        }else{
            var messageSenderName = SharedPref(context).getLoggedInUserName()
            message.senderName=messageSenderName
            database.child(Constant.CHAT_NODE)
                .child(CHAT_ROOM_GROUP)
                .child(MESSAGE_NODE)
                .child(message.messageId)
                .setValue(message)
                .addOnSuccessListener {

                    binding.messageInput.setText("")
                    binding.recyclerView.smoothScrollToPosition(messageAdapter.itemCount)


                }.addOnFailureListener {
                    CommonFunction.errorToast(context,"Message sent failed with error ${it.message}")
                    it.printStackTrace()
                }
        }



    }
    private fun buildChatRoom(){

        val mineUID = FirebaseAuth.getInstance().uid
        val  hisUID = chatItem.chatItem.uid

        if (chatItem.chatItem.chatTYPE!=ChatTYPE.GROUP){
            CHAT_ROOM_MINE = "${mineUID}_${hisUID}"
            CHAT_ROOM_HIS = "${hisUID}_${mineUID}"
        }else{

            CHAT_ROOM_GROUP = chatItem.chatItem.uid
        }

    }
    fun  initRecycleView(context: Context){
        binding.recyclerView.apply {
            layoutManager= LinearLayoutManager(context)
            adapter =messageAdapter
            adapter?.let { smoothScrollToPosition(it.itemCount) }
        }
    }

}
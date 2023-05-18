package com.mkrlabs.pmisstudent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.ChatItemBinding
import com.mkrlabs.pmisstudent.databinding.TaskItemBinding
import com.mkrlabs.pmisstudent.model.ChatItem
import com.mkrlabs.pmisstudent.model.TaskItem
import com.mkrlabs.pmisstudent.util.CommonFunction

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        return  ChatViewHolder(ChatItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        var chatItem = differ.currentList.get(position)

        holder.itemView.apply {
            holder.binding.chatUserNameTV.text = chatItem.name
            holder.binding.chatUserNameRole.text = chatItem.designation
            Glide.with(this)
                .load(chatItem.image)
                .placeholder(R.drawable.empty_person)
                .error(R.drawable.empty_person)
                .into(holder.binding.chatUserImage)
        }


        holder.binding.chatItemCL.setOnClickListener {
            onChatItemClickListener?.invoke(it,chatItem)

        }


    }

    private  var onChatItemClickListener : ((View, ChatItem)->Unit)? = null

    fun setOnChatItemClickListener(listener : (View, ChatItem)->Unit){
        onChatItemClickListener = listener
    }



    private  val  differCallback =object: DiffUtil.ItemCallback<ChatItem>(){

        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return  oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return  oldItem == newItem
        }
    }

    val differ  = AsyncListDiffer(this,differCallback)


    inner  class  ChatViewHolder(val binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root)

}
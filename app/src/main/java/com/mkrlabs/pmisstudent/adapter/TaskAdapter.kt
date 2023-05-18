package com.mkrlabs.pmisstudent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mkrlabs.pmisstudent.databinding.TaskItemBinding
import com.mkrlabs.pmisstudent.databinding.TeamItemBinding
import com.mkrlabs.pmisstudent.model.Project
import com.mkrlabs.pmisstudent.model.TaskItem
import com.mkrlabs.pmisstudent.util.CommonFunction

class TaskAdapter : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        return  TaskViewHolder(TaskItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        var task = differ.currentList.get(position)

        holder.itemView.apply {
            holder.binding.tasDescriptionItemTV.text = task.description
            holder.binding.tasDescriptionItemStatusCB.isChecked = task.status

            CommonFunction.taskDateFormat(task.timestamp){
                holder.binding.taskDeadLineItemTV.text = it
            }

        }

        holder.binding.floatingMenuOnTaskItem.setOnClickListener {
            onTaskMenuItemClickListener?.invoke(it,task)

        }
        holder.binding.tasDescriptionItemStatusCB.setOnCheckedChangeListener { compoundButton, b ->
            onTaskCheckChangeListener?.invoke(compoundButton,b,task)
        }


    }

    private  var onTaskMenuItemClickListener :((View,TaskItem)->Unit)? = null

    fun setOnTaskMenuItemClickListener(listener : (View, TaskItem)->Unit){
        onTaskMenuItemClickListener = listener
    }

    private var onTaskCheckChangeListener:((View,Boolean,TaskItem)->Unit)? =null

    fun setOnCheckChangeListener(listener: (View, Boolean, TaskItem) -> Unit){
        onTaskCheckChangeListener = listener
    }



    private  val  differCallback =object: DiffUtil.ItemCallback<TaskItem>(){

        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return  oldItem == newItem
        }
    }

    val differ  = AsyncListDiffer(this,differCallback)


    inner  class  TaskViewHolder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)

}
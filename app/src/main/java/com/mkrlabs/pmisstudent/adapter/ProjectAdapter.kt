package com.mkrlabs.pmisstudent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mkrlabs.pmisstudent.databinding.TeamItemBinding
import com.mkrlabs.pmisstudent.model.Project

class ProjectAdapter : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {

        return  ProjectViewHolder(TeamItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {

        var project = differ.currentList.get(position)

        holder.itemView.apply {
           // Glide.with(context).load(article.urlToImage).into(holder.binding.ivArticleImage)

            var modifiedProjectName = StringBuilder()

            if (project.projectName.length>25){
                modifiedProjectName.append( project.projectName.substring(0,22))
                modifiedProjectName.append("...")
            }else modifiedProjectName.append( project.projectName)

            holder.binding.teamProjectName.text = modifiedProjectName
            holder.binding.teamProjectType.text = project.projectType

        }
        holder.binding.teamItemCV.setOnClickListener {
            onProjectsItemClickListener?.let {
                it(project)
            }
        }

        holder.binding.directGroupMessageProjectItem.setOnClickListener{
            OnGroupMessageItemClickListener?.let {
                it(project)
            }
        }
    }
    private  var onProjectsItemClickListener :((Project)->Unit)? = null
    fun setOnProjectItemClickListener(listener : (Project)->Unit){
        onProjectsItemClickListener = listener
    }
     private  var OnAddTeamMemberItemClickListener :((Project)->Unit)? = null

    fun setOnAddTeamMemberItemClickListener(listener : (Project)->Unit){
        OnAddTeamMemberItemClickListener = listener
    }




    private  var OnGroupMessageItemClickListener :((Project)->Unit)? = null

    fun  setOnGroupMessageClickListener (listener: (Project)->Unit){
        OnGroupMessageItemClickListener = listener
    }



    private  val  differCallback =object: DiffUtil.ItemCallback<Project>(){

        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return  oldItem.projectUID == newItem.projectUID
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return  oldItem == newItem
        }
    }

    val differ  = AsyncListDiffer(this,differCallback)


    inner  class  ProjectViewHolder(val binding: TeamItemBinding) : RecyclerView.ViewHolder(binding.root)

}
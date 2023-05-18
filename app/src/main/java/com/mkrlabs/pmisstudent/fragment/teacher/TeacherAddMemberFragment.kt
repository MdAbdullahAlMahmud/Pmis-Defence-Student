package com.mkrlabs.pmisstudent.fragment.teacher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.CheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.adapter.MemberAddAdapter
import com.mkrlabs.pmisstudent.databinding.FragmentTeacherAddMemberBinding
import com.mkrlabs.pmisstudent.model.Student
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.view_model.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class TeacherAddMemberFragment : Fragment() {
    lateinit var binding : FragmentTeacherAddMemberBinding
    val project : TeacherAddMemberFragmentArgs by navArgs()
    lateinit var memberAdapter : MemberAddAdapter
    lateinit var  projectViewModel: ProjectViewModel

    lateinit var  studentList: ArrayList<Student>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { binding = FragmentTeacherAddMemberBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memberAdapter = MemberAddAdapter()
        setUpRecycleView()
        studentList = ArrayList<Student>()
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        projectViewModel.fetchTeamMemberSuggestList()
        binding.addMemberBackButton.setOnClickListener { findNavController().navigateUp() }
        projectViewModel.teamMemberSuggestionListState.observe(viewLifecycleOwner, Observer { response ->


            when(response){

                is Resource.Success->{
                    hideLoading()
                    response.data?.let {
                        memberAdapter.differ.submitList(it)
                        memberAdapter.notifyDataSetChanged()
                    }

                }

                is Resource.Loading->{
                    showLoading()

                }

                is Resource.Error->{
                    hideLoading()
                    CommonFunction.successToast(view.context,"Something went wrong")
                }
            }
        })



        memberAdapter.setOnMemberItemClickListener{

            if (!isAlreadyAddedToChipList(it)){
                studentList.add(it)
                addSelectedTeamMemberChipGroup(it)
            }else{
                CommonFunction.infoToast(view.context,"Already added to the list")
            }


        }



        binding.addMemberCreateTeamButton.setOnClickListener {


            projectViewModel.addMemberToProject( project.project.projectUID,studentList)

            projectViewModel.addTeamMemberToProjectState.observe(viewLifecycleOwner, Observer {response ->

                when(response){

                    is Resource.Success->{
                        hideLoading()
                        response.data?.let {

                            CommonFunction.successToast(view.context,it)
                            findNavController().navigateUp()
                        }

                    }

                    is Resource.Loading->{
                        showLoading()

                    }

                    is Resource.Error->{
                        hideLoading()
                        CommonFunction.successToast(view.context,response.message.toString())
                    }
                }


            })



        }
    }

    private fun isAlreadyAddedToChipList(item: Student):Boolean{

        studentList.forEach {student ->
            if (item.uid.equals(student.uid)){
                return  true
            }
        }
        return false

    }

    private fun addSelectedTeamMemberChipGroup(student: Student){
        val chipItem = Chip(context)
        chipItem.text = student.id
        chipItem.isCloseIconVisible = true
        chipItem.setTextColor( resources.getColor(R.color.primaryColor))
        chipItem.isClickable = true
        chipItem.isFocusable = true


        chipItem.setOnCloseIconClickListener {
            studentList.remove(student)
            binding.memberSelectedChipGroup.removeView(it)
        }

        binding.memberSelectedChipGroup.addView(chipItem)

    }

    private fun removeStudentFromList(student: Student){


    }

    private val chipClickListener = View.OnClickListener {

        val anim = AlphaAnimation(1f,0f)
        anim.duration = 250
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                binding.memberSelectedChipGroup.removeView(it)
            }
            override fun onAnimationStart(animation: Animation?) {}
        })
        it.startAnimation(anim)

    }
    private fun showLoading(){
        binding.teacherAddMemberProgressbar.visibility = View.VISIBLE
    }
    private fun hideLoading(){
        binding.teacherAddMemberProgressbar.visibility = View.GONE
    }
    private fun setUpRecycleView(){
        binding.teacherAddMemberRV.apply {
            adapter = memberAdapter
            layoutManager =  LinearLayoutManager(activity)
        }
    }

}
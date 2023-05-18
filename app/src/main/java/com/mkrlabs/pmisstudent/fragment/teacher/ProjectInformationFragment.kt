package com.mkrlabs.pmisstudent.fragment.teacher

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.FragmentProjectInformationBinding
import com.mkrlabs.pmisstudent.model.Project
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.view_model.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProjectInformationFragment : Fragment() {


    lateinit var binding: FragmentProjectInformationBinding

    lateinit var projectViewModel :ProjectViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProjectInformationBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]


        binding.projectInformationNextButton.setOnClickListener{
            findNavController().navigate(R.id.action_projectInformationFragment_to_teacherAddMemberFragment)
        }


        binding.projectInformationBackButton.setOnClickListener {
            findNavController().navigateUp()

        }


        binding.projectInformationNextButton.setOnClickListener{


            val projectName = binding.createProjectProjectName.text.toString()
            val projectId = binding.createProjectProjectId.text.toString()




            if (TextUtils.isEmpty(projectName)){
                binding.createProjectProjectName.error= "required"
                return@setOnClickListener
            }

            if (binding.createProjectProjectTypeGroup.checkedRadioButtonId == -1){
                CommonFunction.errorToast(view.context,"please select project type")
                return@setOnClickListener

            }

            if (TextUtils.isEmpty(projectId)){
                binding.createProjectProjectId.error= "required"
                return@setOnClickListener
            }

            val selectedRadioButton = view.findViewById<RadioButton>( binding.createProjectProjectTypeGroup.checkedRadioButtonId)
            val type: String = selectedRadioButton.text.toString()
            val project = Project("",projectId,type,projectName,"","","","")

           /* projectViewModel.createProject(project)

            projectViewModel.createProjectState.observe(viewLifecycleOwner, Observer { response ->

                when(response){

                    is Resource.Success->{
                        hideLoading()
                        response.data?.let {
                            CommonFunction.successToast(view.context,it)
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




            })*/


            projectViewModel.createProject(project)
            projectViewModel.createProjectState.observe(viewLifecycleOwner, Observer { response ->

                when(response){

                    is Resource.Success->{
                        hideLoading()
                        response.data?.let {
                            CommonFunction.successToast(view.context,it.first.projectUID)

                            val project = it.first
                            val bundle = Bundle().apply {
                                putSerializable("project",project)
                            }
                            Log.v("Project", "Project Pojo ->  ${project.toString()}")
                            findNavController().navigate(R.id.action_projectInformationFragment_to_teacherAddMemberFragment,bundle)
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


    fun hideLoading(){
        binding.projectInformationProgressBar.visibility= View.GONE
    }

    fun showLoading(){
        binding.projectInformationProgressBar.visibility= View.VISIBLE
    }
}
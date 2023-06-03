package com.mkrlabs.pmisstudent.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.adapter.ProjectAdapter
import com.mkrlabs.pmisstudent.databinding.FragmentHomeBinding
import com.mkrlabs.pmisstudent.model.ChatItem
import com.mkrlabs.pmisstudent.model.ChatTYPE
import com.mkrlabs.pmisstudent.ui.HomeActivity
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.util.SharedPref
import com.mkrlabs.pmisstudent.view_model.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    lateinit var mAuth : FirebaseAuth
    lateinit var projectViewModel: ProjectViewModel

    lateinit var projectAdapter: ProjectAdapter

    lateinit var onMenuItemClickListener: OnMenuItemClickListener

    lateinit var sharedPref: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        mAuth  = FirebaseAuth.getInstance()
        sharedPref = SharedPref(view.context)

        binding.logoutButton.setOnClickListener {
            mAuth.signOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }



        CommonFunction.successToast(view.context,SharedPref(view.context).getLoggedInUserName() + " ->  ${sharedPref.getUSER_UID()}")

        projectViewModel.fetchProjectList()
        projectViewModel.projectList.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success->{
                    hideLoading()
                    response.data?.let {
                        projectAdapter.differ.submitList(it)

                        if (it.size>0){
                            Log.v("Project", "Student Project Size -> ${it.size}" )
                            var projectId  = it.get(0).projectUID
                            sharedPref.setStudentProjectId(projectId)
                        }
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

        projectAdapter.setOnProjectItemClickListener { project ->
            val projectIntent = project
            val bundle = Bundle().apply { putSerializable("project",projectIntent) }
            findNavController().navigate(R.id.action_homeFragment_to_projectDetailsFragment,bundle)
        }

        projectAdapter.setOnAddTeamMemberItemClickListener {
            val bundle = Bundle().apply {putSerializable("project",it)}
            findNavController().navigate(R.id.action_homeFragment_to_teacherAddMemberFragment,bundle)
        }


        projectAdapter.setOnGroupMessageClickListener {
            var groupChatItem = ChatItem(it.projectName,it.projectUID, it.projectDescription,
                Date().time,it.teacher_id,"'",
                ChatTYPE.GROUP)

            val bundle = Bundle().apply {
                putSerializable("chatItem",groupChatItem)
            }
            findNavController().navigate(R.id.action_homeFragment_to_chatFragment,bundle)

        }

        binding.homeNavDrawerMenu.setOnClickListener {
            (requireActivity() as HomeActivity).openNavigationDrawer()
        }

    }

    interface  OnMenuItemClickListener{
        fun OnClick()
    }

    fun hideLoading(){
        binding.teacherHomeProgressBar.visibility= View.GONE
    }

    fun showLoading(){
        binding.teacherHomeProgressBar.visibility= View.VISIBLE
    }
    private  fun  setupRecycleView(){

        projectAdapter = ProjectAdapter()

        binding.projectRV.apply {
            adapter = projectAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }

}
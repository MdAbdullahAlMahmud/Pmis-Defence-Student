package com.mkrlabs.pmisstudent.fragment.project_details_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.adapter.ChatAdapter
import com.mkrlabs.pmisstudent.databinding.FragmentChatTabBinding
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.view_model.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatTabFragment(val projectId :String) : Fragment() {

    lateinit var binding : FragmentChatTabBinding
    lateinit var chatAdapter : ChatAdapter
    lateinit var viewModel: ChatViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChatTabBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        initRecycleView()

        viewModel.chatUserList(projectId)
        viewModel.chatUserListState.observe(viewLifecycleOwner, Observer {response->

            when(response){

                is  Resource.Loading->{
                    showLoading()
                }
                is  Resource.Success->{
                    response.data?.let {
                        chatAdapter.differ.submitList(it)
                        hideLoading()
                    }

                }
                is  Resource.Error->{

                    CommonFunction.errorToast(view.context, response.message.toString())

                }
        }

        })

        chatAdapter.setOnChatItemClickListener{ view, chatItem ->

            val chatIntent = chatItem
            val bundle = Bundle().apply {
                putSerializable("chatItem",chatIntent)
            }
            findNavController().navigate(R.id.action_projectDetailsFragment_to_chatFragment,bundle)
        }


    }


    fun  showLoading(){
        binding.chatProgressBar.visibility = View.VISIBLE
    }

    fun  hideLoading(){
        binding.chatProgressBar.visibility = View.GONE
    }



    fun initRecycleView(){


        chatAdapter = ChatAdapter()

        binding.chatRV.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(activity)
        }



    }

}
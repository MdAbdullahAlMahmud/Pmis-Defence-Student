package com.mkrlabs.pmisstudent.fragment.project_details_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.databinding.FragmentOverviewTabBinding
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.view_model.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverviewTabFragment(val projectId:String) : Fragment() {

    lateinit var projectViewModel: ProjectViewModel
    lateinit var binding: FragmentOverviewTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewTabBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]



        projectViewModel.overviewTaskItemState.postValue(Resource.Loading())
        projectViewModel.fetchOverviewTaskSize(projectId)

        projectViewModel.overviewTaskItemState.observe(viewLifecycleOwner, Observer {response->


            when(response){

                is Resource.Success->{
                    response.data?.let {

                        binding.overViewTaskFinishedTV.text = response.data.second.toString()
                        binding.overViewTaskProgressTV.text = (response.data.first - response.data.second).toString()


                        binding.overViewTaskProgress.max= response.data.first
                        binding.overViewTaskProgress.progress = response.data.second

                        var progress = 0
                        try {
                            progress = (100* response.data.second)/response.data.first
                        }catch (e :Exception){
                            progress = 0
                        }

                        val progressInProgress = progress
                        binding.overviewProgressInPercentage.text = "${progressInProgress.toString()}%"

                    }

                }

                is  Resource.Loading->{

                }


                is  Resource.Error->{
                    CommonFunction.errorToast(view.context, response.message.toString())

                }

            }



        })


    }


}
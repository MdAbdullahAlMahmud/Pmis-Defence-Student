package com.mkrlabs.pmisstudent.fragment.project_details_tab

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.mkrlabs.pmisstudent.R
import com.mkrlabs.pmisstudent.adapter.ProjectAdapter
import com.mkrlabs.pmisstudent.adapter.TaskAdapter
import com.mkrlabs.pmisstudent.databinding.CreateTaskBottomSheetBinding
import com.mkrlabs.pmisstudent.databinding.FragmentTeacherTaskAddBinding
import com.mkrlabs.pmisstudent.fragment.ProjectDetailsFragment
import com.mkrlabs.pmisstudent.model.Project
import com.mkrlabs.pmisstudent.model.TaskItem
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Resource
import com.mkrlabs.pmisstudent.view_model.ProjectViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject
@AndroidEntryPoint
class TeacherTaskAddFragment (val  projectId :String): Fragment() {

    lateinit var binding : FragmentTeacherTaskAddBinding
    lateinit var taskBinding : CreateTaskBottomSheetBinding

    lateinit var  projectViewModel: ProjectViewModel
    lateinit var  taskAdapter: TaskAdapter

    lateinit var  bottomSheetDialog: BottomSheetDialog
    private lateinit var selectedDate: Date


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeacherTaskAddBinding.inflate(layoutInflater)
        return  binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initBottomSheet(view)

        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]





        fetchTaskList()


        taskAdapter.setOnTaskMenuItemClickListener { view, taskItem->

            val popupMenu = PopupMenu(context,view)
            popupMenu.menuInflater.inflate(R.menu.pop_up_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.taskPopUpMenuEditIcon ->{


                        taskUpsert(true,taskItem)

                    }
                    R.id.taskPopUpMenuDeleteIcon ->{
                        projectViewModel.deleteTask(projectId,taskItem)
                        deleteTaskItemListener(view.context)
                    }

                }
                true
            })
            popupMenu.show()
        }


        taskAdapter.setOnCheckChangeListener { view, b, taskItem ->

            taskItem.status =b
            taskItem.checkedBy = CommonFunction.loggedInUserUID()
           projectViewModel.updateTask(projectId,taskItem)

        }

        upsertListener(view)

    }


    private fun upsertListener(view: View){
        projectViewModel.createTaskItemState.observe(viewLifecycleOwner, Observer {response->

            when(response){

                is Resource.Success->{
                    hideLoading()
                    response.data?.let {
                        bottomSheetDialog.dismiss()

                        CommonFunction.successToast(view.context,it)
                        fetchTaskList()
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

        projectViewModel.editTaskItemState.observe(viewLifecycleOwner, Observer {response->

            when(response){

                is Resource.Success->{
                    hideLoading()
                    response.data?.let {
                        bottomSheetDialog.dismiss()
                        CommonFunction.successToast(view.context,it)
                        fetchTaskList()
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
    private fun taskUpsert(isEdit: Boolean,taskItem: TaskItem){

        bottomSheetDialog.show()

        if (isEdit){

            taskBinding.createTaskAddTaskButton.text = "Update Task"
            taskBinding.createTaskTaskDescriptionEdt.setText(taskItem.description)
            selectedDate = Date(taskItem.timestamp)
            projectViewModel.taskDateFormat(selectedDate){
                taskBinding.taskDateTV.text=it
            }

        }else{
            taskBinding.createTaskAddTaskButton.text = "Create Task"
            taskBinding.createTaskTaskDescriptionEdt.setText(taskItem.description)
            taskBinding.taskDateTV.text="Select Date"
            selectedDate = Date()

        }



        taskBinding.createTaskDateCV.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(selectedDate.time)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                selectedDate = Date(it)
                projectViewModel.taskDateFormat(selectedDate){
                    taskBinding.taskDateTV.text = it
                }
            }

            datePicker.show(childFragmentManager, "tag")

        }



        taskBinding.createTaskAddTaskButton.setOnClickListener {

            val taskDescription = taskBinding.createTaskTaskDescriptionEdt.text.toString()
            if (taskDescription.isEmpty()){
                taskBinding.createTaskTaskDescriptionEdt.error = "required"
                return@setOnClickListener
            }



            val upsertTaskItem = TaskItem()
            upsertTaskItem.id=""
            upsertTaskItem.description=taskDescription
            upsertTaskItem.timestamp =selectedDate.time
            upsertTaskItem.status = false

            if (isEdit){
                upsertTaskItem.id = taskItem.id
                projectViewModel.editTaskItemState.postValue(Resource.Loading())
                projectViewModel.editTask(projectId,upsertTaskItem)

            }else{
                //Insert
                projectViewModel.createTaskItemState.postValue(Resource.Loading())
                projectViewModel.createNewTask(projectId,upsertTaskItem)
            }
        }
    }
    private fun  editTaskItem(context: Context, taskItem:TaskItem){
        bottomSheetDialog.show()

        taskBinding.createTaskTaskDescriptionEdt.setText(taskItem.description)

        taskBinding.createTaskAddTaskButton.text= "Update"

        taskBinding.createTaskAddTaskButton.setOnClickListener {


            val taskDescription = taskBinding.createTaskTaskDescriptionEdt.text.toString()
            if (taskDescription.isEmpty()){
                taskBinding.createTaskTaskDescriptionEdt.error = "required"
                return@setOnClickListener
            }
            val  taskItem = TaskItem(taskDescription,taskItem.id,Date().time,false)

            projectViewModel.editTaskItemState.postValue(Resource.Loading())
            projectViewModel.editTask(projectId,taskItem)

        }

        projectViewModel.editTaskItemState.observe(viewLifecycleOwner, Observer { response ->

            when(response){
                is Resource.Success->{
                    response.data?.let {
                        bottomSheetDialog.dismiss()
                        fetchTaskList()
                        CommonFunction.successToast(context,it)
                    }
                }

                is Resource.Loading->{
                }
                is Resource.Error->{
                    bottomSheetDialog.dismiss()
                    CommonFunction.errorToast(context,response.message.toString())
                }
            }



        })

    }

    private fun deleteTaskItemListener(context: Context){
        projectViewModel.deleteTaskItemState.postValue(Resource.Loading())
        projectViewModel.deleteTaskItemState.observe(viewLifecycleOwner, Observer {response->

            when(response){

                is Resource.Success->{
                    response.data?.let {
                        fetchTaskList()
                        CommonFunction.successToast(context,it)

                    }
                }

                is Resource.Loading->{
                }
                is Resource.Error->{
                    CommonFunction.successToast(context,response.message.toString())
                }
            }
        })
    }
    fun hideLoading(){
        taskBinding.createTaskProgressBar.visibility= View.GONE
    }

    fun showLoading(){
        taskBinding.createTaskProgressBar.visibility= View.VISIBLE
    }

    fun hideTaskLoading(){
        binding.teacherAddTaskProgressBar.visibility= View.GONE
    }

    fun showTaskLoading(){
        binding.teacherAddTaskProgressBar.visibility= View.VISIBLE
    }



    private fun  init(){
        setupRecycleView()
    }
    private fun initBottomSheet(view: View){
        val inflater = LayoutInflater.from(requireContext())
        taskBinding = CreateTaskBottomSheetBinding.inflate(inflater)

        bottomSheetDialog = BottomSheetDialog(view.context)

        bottomSheetDialog.setContentView(taskBinding.root)
    }


    private fun fetchTaskList(){
        projectViewModel.taskItemList.postValue(Resource.Loading())
        projectViewModel.fetchTaskList(projectId)
        projectViewModel.taskItemList.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success->{
                    hideTaskLoading()
                    response.data?.let {
                        taskAdapter.differ.submitList(it)
                    }
                }
                is Resource.Loading->{
                    showTaskLoading()
                }
                is Resource.Error->{
                    hideTaskLoading()
                    context?.let { CommonFunction.successToast(it,response.message.toString()) }
                }
            }
        })
    }
    private  fun  setupRecycleView(){

        taskAdapter = TaskAdapter()

        binding.taskRV.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }


}
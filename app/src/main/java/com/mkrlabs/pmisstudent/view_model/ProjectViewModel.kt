package com.mkrlabs.pmisstudent.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mkrlabs.pmisstudent.model.*
import com.mkrlabs.pmisstudent.repository.AuthRepository
import com.mkrlabs.pmisstudent.repository.ProjectRepository
import com.mkrlabs.pmisstudent.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(val repository: ProjectRepository, val  authRepository: AuthRepository,val mAuth : FirebaseAuth) :ViewModel() {

    var createProjectState : MutableLiveData<Resource<Pair<Project,String>>> = MutableLiveData()
    var projectList : MutableLiveData<Resource<List<Project>>> = MutableLiveData()
    var taskItemList : MutableLiveData<Resource<List<TaskItem>>> = MutableLiveData()
    var createTaskItemState : MutableLiveData<Resource<String>> = MutableLiveData()
    var deleteTaskItemState : MutableLiveData<Resource<String>> = MutableLiveData()
    var editTaskItemState : MutableLiveData<Resource<String>> = MutableLiveData()
    var overviewTaskItemState : MutableLiveData<Resource<Pair<Int,Int>>> = MutableLiveData()
    var teamMemberSuggestionListState : MutableLiveData<Resource<List<Student>>> = MutableLiveData()
    var addTeamMemberToProjectState : MutableLiveData<Resource<String>> = MutableLiveData()
    var sendNotificationState : MutableLiveData<Resource<String>> = MutableLiveData()


    fun sendNotificationToSpecificGroup(notificationItem: NotificationItem){

        sendNotificationState.postValue(Resource.Loading())
        val headerToken = "key=AAAAB_JBOTM:APA91bH_tPW4zfX5rXXuEQ0bA7BQvbL9UJl8V8-9TuETdqFBuK2KC6oB5GHScAs4XBE1KVudYur4TkWvxEYXK6aw-ZujVn6hYWl6vt9LfcITCngFYt9JzFSkHOjW_eB_cSfF74bpmxGL"
        viewModelScope.launch {
            val response = repository.sendNotification(notificationItem = notificationItem,
                headerToken = headerToken)
            handleResponse(response)
        }
    }
    private  fun  handleResponse(response : Response<NotificationResponse>){
        if (response.isSuccessful){
            response.body()?.let {result->

                val responseMessage = result.let {
                    it
                }

                sendNotificationState.postValue(Resource.Success("Success Response"))

            }
        }else{
            sendNotificationState.postValue(Resource.Error(response.message()))
        }

    }
    fun createProject(project: Project){
        createProjectState.value = Resource.Loading()
        viewModelScope.launch{
            repository.createProject(project){
                createProjectState.postValue(it)
            }
        }
    }


    fun  createNewTask(projectId : String ,taskItem: TaskItem){
        createTaskItemState.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.addTaskToProject(projectId,taskItem){
                createTaskItemState.postValue(it)
            }
        }
    }
    fun  deleteTask(projectId : String ,taskItem: TaskItem){
        deleteTaskItemState.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.deleteTaskFromProject(projectId,taskItem.id){
                deleteTaskItemState.postValue(it)
            }
        }
    }


    fun  editTask(projectId : String ,taskItem: TaskItem){
        editTaskItemState.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.editTaskFromProject(projectId,taskItem){
                editTaskItemState.postValue(it)
            }
        }
    }

    fun  fetchProjectList(){
        projectList.postValue(Resource.Loading())

            mAuth.currentUser?.let {


                viewModelScope.launch{
                    authRepository.getUserInfo {
                        it?.let {
                            if (it.type==UserType.STUDENT){

                                Log.v("Type","Student")

                                viewModelScope.launch {
                                    repository.getAllProjectUnderStudent(it.uid){
                                        projectList.postValue(it)
                                    }
                                }
                            }else{
                                Log.v("Type","Teacher")

                                viewModelScope.launch {
                                    repository.getAllProjectUnderTeacher(it.uid){
                                        projectList.postValue(it)
                                    }
                                }

                            }
                        }
                    }
                }




            }

    }

    fun updateTask (projectId: String,taskItem: TaskItem){
        viewModelScope.launch {
            repository.updateTask(projectId,taskItem){}
        }
    }
    fun  fetchTaskList(projectId: String){
        taskItemList.postValue(Resource.Loading())
        viewModelScope.launch {
                repository.getAllTaskListOfAProject(projectId){
                    taskItemList.postValue(it)
                }
        }
    }
    fun  fetchOverviewTaskSize(projectId: String){
        overviewTaskItemState.postValue(Resource.Loading())
        viewModelScope.launch {
                repository.getAllTaskOverviewOfAProject(projectId){
                    overviewTaskItemState.postValue(it)
                }
        }
    }



    fun taskDateFormat(selectedDate: Date ,response : (String)->Unit){
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val formattedDate = formatter.format(selectedDate)
        response.invoke(formattedDate)
    }


    fun fetchTeamMemberSuggestList(){
        teamMemberSuggestionListState.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.getAllTeamMemberListSuggestion {
                teamMemberSuggestionListState.postValue(it)
            }
        }
    }


    fun addMemberToProject(projectId: String , list : List<Student>){
        addTeamMemberToProjectState.postValue(Resource.Loading())
        viewModelScope.launch {
            repository.addMemberToProject(projectId,list){
                addTeamMemberToProjectState.postValue(it)
            }
        }

    }


}
package com.mkrlabs.pmisstudent.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mkrlabs.pmisstudent.model.Student
import com.mkrlabs.pmisstudent.model.Teacher
import com.mkrlabs.pmisstudent.model.User
import com.mkrlabs.pmisstudent.repository.AuthRepository
import com.mkrlabs.pmisstudent.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor (val authRepository: AuthRepository) : ViewModel() {

    val authState : MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()
    val loginState : MutableLiveData<Resource<FirebaseUser>> = MutableLiveData()

    val savedUserData : MutableLiveData<Resource<String>> = MutableLiveData()
    val userWithUIDData : MutableLiveData<Resource<User>> = MutableLiveData()


    fun createUserAccount( student : Student) {
        authState.postValue(Resource.Loading())
        viewModelScope.launch {
           val firebaseUser =  authRepository.crateUserAccount(student).data
            if (firebaseUser!=null){

                authState.postValue(Resource.Success(firebaseUser))
                student.uid = firebaseUser.uid
                saveUserData(student)
            }else{
                authState.postValue(Resource.Error("Something went wrong"))
            }
        }

    }


    fun  loginUser(email:String, password :String){
        loginState.postValue(Resource.Loading())
        viewModelScope.launch {
            val firebaseUser =  authRepository.loginUserWithEmailAndPassword(email,password).data
            if (firebaseUser!=null){
                loginState.postValue(Resource.Success(firebaseUser))
            }else{
                loginState.postValue(Resource.Error("Something went wrong"))
            }
        }

    }


    fun saveUserData(student: Student){
        savedUserData.postValue(Resource.Loading())
        viewModelScope.launch {
            val response = authRepository.saveUserAccount(student)
            savedUserData.postValue(Resource.Success("Data Saved successfully"))
        }
    }

    fun getSpecificUserWithUID(uid:String){
        userWithUIDData.postValue(Resource.Loading())
        viewModelScope.launch {
            authRepository.getUserInfoWithUID(uid){
                userWithUIDData.postValue(it)
            }
        }
    }



}
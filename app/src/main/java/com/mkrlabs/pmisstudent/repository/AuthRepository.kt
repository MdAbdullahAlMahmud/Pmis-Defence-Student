package com.mkrlabs.pmisstudent.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mkrlabs.pmisstudent.model.Student
import com.mkrlabs.pmisstudent.model.User
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Constant
import com.mkrlabs.pmisstudent.util.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(val mAuth: FirebaseAuth, val  firebaseFirestore: FirebaseFirestore){



    suspend fun crateUserAccount(student: Student): Resource<FirebaseUser> {
        try {
            val result =  mAuth.createUserWithEmailAndPassword(student.email,student.password).await()
          return  Resource.Success(result.user!!)


        }catch (e :Exception){
            e.printStackTrace()
            return Resource.Error(e.localizedMessage,null)

        }

    }
    suspend fun loginUserWithEmailAndPassword(email:String, password :String): Resource<FirebaseUser> {
        try {
            val result =  mAuth.signInWithEmailAndPassword(email,password).await()
          return  Resource.Success(result.user!!)


        }catch (e :Exception){
            e.printStackTrace()
            return Resource.Error(e.localizedMessage,null)

        }

    }




    suspend fun saveUserAccount(student: Student)  = firebaseFirestore.collection(Constant.USER_NODE).document(student.uid).set(student).await()


    suspend fun getUserInfo(result: (User?) ->Unit) {
        val loggedInUser = CommonFunction.loggedInUserUID()
        firebaseFirestore.collection(Constant.USER_NODE)
            .document(loggedInUser).get().addOnSuccessListener {

                it.toObject(User::class.java)?.let {
                    result.invoke(it)
                }

            }.addOnFailureListener {
                result.invoke(null)
            }
    }

    suspend fun getUserInfoWithUID(uid:String , result: (Resource<User>) ->Unit) {
        firebaseFirestore.collection(Constant.USER_NODE)
            .document(uid).get().addOnSuccessListener {

                it.toObject(User::class.java)?.let {
                    result.invoke(Resource.Success(it))
                }

            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }
    }









}
package com.mkrlabs.pmisstudent.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPref(context: Context) {

    private lateinit var sharedPref :SharedPreferences

    private val USER_NAME_KEY = "USER_NAME_KEY"
    private val USER_PROJECT_ID = "USER_PROJECT_ID"
    private val USER_UID = "USER_UID"
    private val IS_SUBSCRIBED_ON_TOPIC = "IS_SUBSCRIBED_ON_TOPIC"

    init {
        sharedPref = context.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    public fun getLoggedInUserName():String{
        return sharedPref.getString(USER_NAME_KEY,"Empty").toString()
    }

    public fun setLoggedInUserName(name:String){
        sharedPref.edit().putString(USER_NAME_KEY,name).apply()
    }

     public fun getStudentProjectId():String{
        return sharedPref.getString(USER_PROJECT_ID,"Empty").toString()
    }

    public fun setStudentProjectId(projectId:String){
        sharedPref.edit().putString(USER_PROJECT_ID,projectId).apply()
    }

    public fun getUSER_UID():String{
        return sharedPref.getString(USER_UID,"Empty").toString()
    }

    public fun setStudenUSER_UID(uid:String){
        sharedPref.edit().putString(USER_UID,uid).apply()
    }


     public fun getSubscribedStatus():Boolean{
        return sharedPref.getBoolean(IS_SUBSCRIBED_ON_TOPIC,false)
    }

    public fun setSubscribedStatus(status : Boolean){
        sharedPref.edit().putBoolean(IS_SUBSCRIBED_ON_TOPIC,status).apply()
    }










}
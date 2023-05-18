package com.mkrlabs.pmisstudent.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPref(context: Context) {

    private lateinit var sharedPref :SharedPreferences

    private val USER_NAME_KEY = "USER_NAME_KEY"

    init {
        sharedPref = context.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    public fun getLoggedInUserName():String{
        return sharedPref.getString(USER_NAME_KEY,"Empty").toString()
    }

    public fun setLoggedInUserName(name:String){
        sharedPref.edit().putString(USER_NAME_KEY,name).apply()
    }


}
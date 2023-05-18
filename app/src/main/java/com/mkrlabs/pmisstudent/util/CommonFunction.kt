package com.mkrlabs.pmisstudent.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

object CommonFunction {


    fun successToast(context: Context ,msg : String){
        Toasty.success(context,msg,Toasty.LENGTH_SHORT,true).show()
    }

     fun infoToast(context: Context ,msg : String){
        Toasty.info(context,msg,Toasty.LENGTH_SHORT,true).show()
    }

    fun errorToast(context: Context ,msg : String){
        Toasty.error(context,msg,Toasty.LENGTH_SHORT,true).show()
    }



    fun loggedInUserUID() : String{
        return FirebaseAuth.getInstance().currentUser?.uid.toString()

    }
    fun taskDateFormat(selectedDate: Long, response : (String)->Unit){
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val formattedDate = formatter.format(Date(selectedDate))
        response.invoke(formattedDate)
    }
}
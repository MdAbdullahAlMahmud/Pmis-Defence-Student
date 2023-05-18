package com.mkrlabs.pmisstudent.model

data class TaskItem(var description : String = "", var  id : String = "", var timestamp :Long = 0 ,var status : Boolean = false,var checkedBy : String = "")

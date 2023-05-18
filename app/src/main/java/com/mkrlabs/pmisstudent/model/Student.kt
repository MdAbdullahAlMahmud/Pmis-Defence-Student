package com.mkrlabs.pmisstudent.model

import com.mkrlabs.pmisstudent.model.User

open class Student : User {


       var image : String =""
       var batchId : String = ""
    var projectId : String?= null

    constructor()
     constructor(name:String, email:String, password:String, id :String, uid : String,userType: UserType) : super(name,email,password,id,uid,userType)
     constructor(name:String, email:String, password:String, id :String, uid : String , image :String, batchId : String ,type: UserType) : this(name,email,password,id,uid, type){
                 this.image = image
                 this.batchId = batchId
                 this.type = type
             }
    constructor(name:String, email:String, password:String, id :String, uid : String , image :String, batchId : String ,type: UserType,projectId : String) : this(name,email,password,id,uid,type){
                 this.image = image
                 this.batchId = batchId
                 this.projectId = projectId
                 this.type = type
             }




    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

 }


     /**
     var name:String,
    var email :String   ,
    var password:String ,
    var id :String ,
    var uid:String ,
    var  designation:String ,
    var phone:String,
    var image: String ,
    var batchId: String

    **/

/**

val name:String,
val email :String,
val  password:String,
val id :String,
var uid:String

 **/
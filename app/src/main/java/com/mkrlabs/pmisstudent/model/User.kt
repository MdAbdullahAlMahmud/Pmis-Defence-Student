package com.mkrlabs.pmisstudent.model
open class User{
     var name:String= ""
     var email :String= ""
     var  password:String= ""
     var id :String= ""
     var uid:String= ""
    var type: UserType? =null

     constructor()

     constructor(name: String,email: String , password : String, id : String, uid : String, userType: UserType){
         this.name = name
         this.email = email
         this.password = password
         this.id = id
         this.uid = uid
         this.type = userType
     }

 }



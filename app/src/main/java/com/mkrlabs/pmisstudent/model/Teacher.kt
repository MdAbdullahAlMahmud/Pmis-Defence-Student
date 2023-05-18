package com.mkrlabs.pmisstudent.model


open class Teacher : User{



    var image: String? =null
    var room: String? =null

    constructor()
    constructor(name:String, email:String, password:String, id :String, uid : String,type: UserType) : super(name,email,password,id,uid,type)
    constructor(name:String, email:String, password:String, id :String, uid : String , image :String, room : String ,type: UserType) : this(name,email,password,id,uid,type){
        this.image = image
        this.room = room
        this.type = type
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}
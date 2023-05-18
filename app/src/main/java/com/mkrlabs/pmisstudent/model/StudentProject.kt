package com.mkrlabs.pmisstudent.model

data class StudentProject (
    var  projecetId :String ?= "",
    var  addedBy :String ?= ""
        ){
    override fun toString(): String {
        return "StudentProject(projecetId=$projecetId, addedBy=$addedBy)"
    }


}
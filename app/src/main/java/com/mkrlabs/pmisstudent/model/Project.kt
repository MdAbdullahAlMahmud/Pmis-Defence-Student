package com.mkrlabs.pmisstudent.model

import java.io.Serializable

data class Project(
    var projectUID :String = "",
    var projectID :String = "",
    var projectType : String= "",
    var projectName :String= "",
    var projectDescription :String= "",
    var projectMotivation :String= "",
    var projectObjective :String= "",
    var teacher_id :String= "",
    ):Serializable {
    override fun toString(): String {
        return "Project(projectUID='$projectUID', projectID='$projectID', projectType='$projectType', projectName='$projectName', projectDescription='$projectDescription', projectMotivation='$projectMotivation', projectObjective='$projectObjective', teacher_id='$teacher_id')"
    }
}

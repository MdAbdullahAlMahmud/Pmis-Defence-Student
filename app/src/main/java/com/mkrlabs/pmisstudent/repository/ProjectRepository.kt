package com.mkrlabs.pmisstudent.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mkrlabs.pmisstudent.model.*
import com.mkrlabs.pmisstudent.util.CommonFunction
import com.mkrlabs.pmisstudent.util.Constant
import com.mkrlabs.pmisstudent.util.Resource
import java.util.Date
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    val firebaseFirestore: FirebaseFirestore,
    val mAuth: FirebaseAuth
) {
    suspend fun createProject(
        project: Project,
        result: (Resource<Pair<Project, String>>) -> Unit
    ) {

        val uniqueProjectUID = firebaseFirestore.collection(Constant.PROJECT_NODE).document().id
        project.projectUID = uniqueProjectUID
        mAuth.currentUser?.let {
            project.teacher_id = it.uid
        }

        firebaseFirestore.collection(Constant.PROJECT_NODE).document(uniqueProjectUID).set(project)
            .addOnSuccessListener {
                var loggedInTeacherUID = CommonFunction.loggedInUserUID()

                firebaseFirestore.collection(Constant.USER_NODE).document(loggedInTeacherUID).get().addOnSuccessListener {

                    val  documentSnapshot = it
                     documentSnapshot.toObject(Teacher::class.java)?.let {
                        val teacher = it
                         var addTeacherToProjectChatItem = ChatItem(teacher.name,teacher.uid,"Supervisor",Date().time,teacher.uid,teacher.image.toString(),ChatTYPE.NORMAL)
                         firebaseFirestore.collection(Constant.PROJECT_NODE)
                             .document(uniqueProjectUID)
                             .collection(Constant.TEAM_MEMBER_NODE)
                             .document(teacher.uid)
                             .set(addTeacherToProjectChatItem).addOnSuccessListener {

                                 var uniqueGroupID = firebaseFirestore.collection(Constant.PROJECT_NODE).document().id

                                 var groupItem = ChatItem("Project Group Chat ",uniqueProjectUID,"with teacher",Date().time,teacher.uid,"",ChatTYPE.GROUP)

                                 firebaseFirestore.collection(Constant.PROJECT_NODE)
                                     .document(uniqueProjectUID)
                                     .collection(Constant.GROUP_NODE)
                                     .document(uniqueProjectUID)
                                     .set(groupItem)
                                 result.invoke(Resource.Success(Pair(project, "Project created successfully")))
                             }.addOnFailureListener {
                                 it.printStackTrace()
                                 result.invoke(
                                     Resource.Error(it.localizedMessage.toString())
                                 )
                             }

                     }


                }.addOnFailureListener {
                    it.printStackTrace()
                    result.invoke(
                        Resource.Error(it.localizedMessage.toString())
                    )
                }


            }.addOnFailureListener {
                it.printStackTrace()
            result.invoke(
                Resource.Error(it.localizedMessage.toString())
            )
        }
    }


    suspend fun getAllProjectUnderTeacher(
        teacher_uid:String,
        result: (Resource<List<Project>>) ->Unit
    ){
        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .whereEqualTo("teacher_id",teacher_uid)
            .get()
            .addOnSuccessListener {
                val projects = arrayListOf<Project>()
                for (document in it) {
                    val project = document.toObject(Project::class.java)
                    projects.add(project)
                }
                var userList = ArrayList<Student>()
                firebaseFirestore.collection(Constant.USER_NODE)
                    .get().addOnSuccessListener {

                        for (document in it) {
                            val user = document.toObject(Student::class.java)
                            userList.add(user)                        }


                    }.addOnFailureListener {
                        it.printStackTrace()
                        result.invoke(
                            Resource.Error(
                                it.localizedMessage
                            )
                        )
                    }
                result.invoke(
                    Resource.Success(projects)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Error(
                        it.localizedMessage
                    )
                )
            }

    }

    suspend fun getAllProjectUnderStudent(
        student_uid:String,
        result: (Resource<List<Project>>) ->Unit
    ){
        firebaseFirestore.collection(Constant.STUDENT_PROJECT_NODE)
            .document(student_uid)
            .get()
            .addOnSuccessListener {


                it.toObject(StudentProject::class.java)?.let {studentProject->
                    println("Student Project ${studentProject.toString()}")
                    firebaseFirestore.collection(Constant.PROJECT_NODE)
                        .get()
                        .addOnSuccessListener {
                            val projects = arrayListOf<Project>()
                            for (document in it) {

                                println(it.toString())
                                val project = document.toObject(Project::class.java)
                                println("S -> ${studentProject.projecetId} == ${project.projectUID}")
                                if (project.projectUID.equals(studentProject.projecetId)){
                                    projects.add(project)
                                }
                            }

                            result.invoke(
                                Resource.Success(projects)
                            )
                        }
                        .addOnFailureListener {
                            result.invoke(
                                Resource.Error(
                                    it.localizedMessage
                                )
                            )
                        }


                }

            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Error(
                        it.localizedMessage
                    )
                )
            }

    }




    suspend fun  addTaskToProject(projectId : String , task: TaskItem,
                                  result: (Resource<String>) -> Unit
    ){
        val taskUniqueId = firebaseFirestore.collection(Constant.PROJECT_NODE).document(projectId).collection(Constant.TASK_NODE).document().id

        task.id = taskUniqueId
        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TASK_NODE)
            .document(taskUniqueId)
            .set(task).addOnSuccessListener {
                result.invoke(
                    Resource.Success("Task Added Successfully")
                )
            }.addOnFailureListener {
            result.invoke(
                Resource.Error(it.localizedMessage.toString())
            )

            }
    }


    suspend fun deleteTaskFromProject(projectId: String,taskId : String, result: (Resource<String>) -> Unit){
        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TASK_NODE)
            .document(taskId)
            .delete().addOnSuccessListener {
                result.invoke(Resource.Success("Task Removed Successfully"))

            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }
    }

     suspend fun editTaskFromProject(projectId: String,task: TaskItem, result: (Resource<String>) -> Unit){

        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TASK_NODE)
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                result.invoke(Resource.Success("Task Edit Successfully"))

            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }
    }


    suspend fun updateTask(projectId: String, task: TaskItem , result: (Resource<String>) -> Unit){

        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TASK_NODE)
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                result.invoke(Resource.Success("Task updated successfully"))
            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))

            }
    }

    suspend fun getAllTaskListOfAProject(
        projectId:String,
        result: (Resource<List<TaskItem>>) ->Unit
    ){



        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TASK_NODE)
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val taskItems = arrayListOf<TaskItem>()
                for (document in it) {
                    val taskItem = document.toObject(TaskItem::class.java)
                    taskItems.add(taskItem)
                }
                result.invoke(
                    Resource.Success(taskItems)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Error(
                        it.localizedMessage
                    )
                )
            }

    }


    suspend fun getAllTaskOverviewOfAProject(
        projectId:String,
        result: (Resource<Pair<Int,Int>>) ->Unit
    ){
        firebaseFirestore.collection(Constant.PROJECT_NODE)
            .document(projectId)
            .collection(Constant.TASK_NODE)
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val taskItems = arrayListOf<TaskItem>()
                var countCompletedTask = 0
                for (document in it) {
                    val taskItem = document.toObject(TaskItem::class.java)

                    if (taskItem.status)countCompletedTask++
                    taskItems.add(taskItem)
                }
                result.invoke(
                    Resource.Success(Pair(taskItems.size,countCompletedTask))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Error(
                        it.localizedMessage
                    )
                )
            }
    }


    suspend fun getAllTeamMemberListSuggestion(
        result : (Resource<List<Student>>) -> Unit) {

        firebaseFirestore.collection(Constant.USER_NODE)
            .get()
            .addOnSuccessListener {
                val user_list = arrayListOf<Student>()
                for (document in it) {
                    val team = document.toObject(Student::class.java)
                    if (team.type==UserType.STUDENT){
                        user_list.add(team)
                    }
                }
                result.invoke(
                    Resource.Success(user_list)
                )
            }
            .addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage.toString()))
            }
    }



    suspend fun addMemberToProject(projectId : String , list :  List<Student> , result : (Resource<String>) -> Unit){

       list.forEach {student ->

           var loggedInUser = CommonFunction.loggedInUserUID()
           var chatItem = ChatItem(student.name,student.uid,UserType.STUDENT.name,Date().time,loggedInUser,student.image)


           firebaseFirestore.collection(Constant.PROJECT_NODE)
               .document(projectId)
               .collection(Constant.TEAM_MEMBER_NODE)
               .document(student.uid)
               .set(chatItem).addOnSuccessListener{

                   var  studentProject = StudentProject(projectId,loggedInUser)
                   firebaseFirestore.collection(Constant.STUDENT_PROJECT_NODE)
                       .document(student.uid)
                       .set(studentProject)



               }.addOnFailureListener{
                   it.printStackTrace()
                   result.invoke(Resource.Error(it.localizedMessage.toString()))
               }
           }
            result.invoke(Resource.Success("Successfully"))

    }






}
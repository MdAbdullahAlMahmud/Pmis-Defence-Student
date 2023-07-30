package com.mkrlabs.pmisstudent.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mkrlabs.pmisstudent.model.InformationModel
import com.mkrlabs.pmisstudent.util.Resource
import javax.inject.Inject


/**
 * Created by Abdullah on 7/30/2023.
 */

class InformationRepository @Inject constructor(val firebaseFirestore : FirebaseFirestore){


    suspend fun getDeveloperInformation(
        result : (Resource<InformationModel>) -> Unit
    ){

        firebaseFirestore.collection("information")
            .document("deatils")
            .get().addOnSuccessListener {documentSnapshot->

            documentSnapshot.toObject(InformationModel::class.java)?.let {
                result.invoke(Resource.Success(it))
            }
            }.addOnFailureListener {
                result.invoke(Resource.Error(it.localizedMessage))
            }

    }
}
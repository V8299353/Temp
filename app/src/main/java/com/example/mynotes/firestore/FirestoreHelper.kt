package com.example.mynotes.firestore

import com.example.mynotes.TaskModel
import com.example.mynotes.auth.AuthenticationHelper
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreHelper {

    private var firestore:FirebaseFirestore = FirebaseFirestore.getInstance()

    fun addNewDocument(taskModel: TaskModel,uuid:String): Task<Void> {
        return firestore.collection(AuthenticationHelper().getCurrentUser()!!.uid).document(uuid).set(taskModel)
    }

    fun getAllNotes(): CollectionReference {
        return firestore.collection(AuthenticationHelper().getCurrentUser()!!.uid)
    }

    fun deleteNotes(uuid:String): Task<Void> {
        return firestore.collection(AuthenticationHelper().getCurrentUser()!!.uid).document(uuid).delete()
    }

}
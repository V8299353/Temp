package com.example.mynotes.storage

import android.net.Uri
import android.util.Log
import com.example.mynotes.auth.AuthenticationHelper
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class StorageHelper {

    private val firebaseStorage = FirebaseStorage.getInstance()

    private val profilePhotoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("profileImages")

    private val notesPhotoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("notesImage")

    fun uploadImage(imageUri: Uri): UploadTask? {
        if(AuthenticationHelper().isUserLoggedIn()) {
            return profilePhotoStorage.child(AuthenticationHelper().getCurrentUser()!!.uid).putFile(imageUri)
        }
        return null
    }

    fun uploadNotesImage(imageUri: Uri,fileName:String): UploadTask? {
        if(AuthenticationHelper().isUserLoggedIn()) {
            return notesPhotoStorage.child(AuthenticationHelper().getCurrentUser()!!.uid).child(fileName).putFile(imageUri)
        }
        return null
    }

    fun getNotesImageStorageReference(userId: String,fileName: String):StorageReference {
        return notesPhotoStorage.child(userId).child(fileName)
    }

    fun deleteImageWithUrl(url:String): Task<Void> {
        return firebaseStorage.getReferenceFromUrl(url).delete()
    }

    fun deleteImageWithReference(storageReference: StorageReference) {
        storageReference.delete()
    }

    fun getProfileStorageReference(userId:String): StorageReference {
        return profilePhotoStorage.child(userId)
    }

}
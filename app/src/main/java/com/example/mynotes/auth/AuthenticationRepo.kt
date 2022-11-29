package com.example.mynotes.auth

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest


class AuthenticationHelper {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun signUn(email:String,password:String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email,password)
    }

    fun login(email:String,password:String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email,password)
    }

    fun updateProfile(name:String?,uri:String?): Task<Void>? {
        val profileUpdates = userProfileChangeRequest {
            name?.let {
                displayName = name
            }
            uri?.let {
                photoUri = Uri.parse(uri)
            }
        }
        return getCurrentUser()?.updateProfile(profileUpdates)
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser() :FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun isUserLoggedIn():Boolean {
        getCurrentUser()?.let {
            return true
        } ?: return false
    }


}
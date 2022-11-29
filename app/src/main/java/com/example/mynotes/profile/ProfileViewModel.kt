package com.example.mynotes.profile

import android.R
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class ProfileViewModel : ViewModel() {

    private val isUpdateSuccess = MutableLiveData(false)

    fun signOutUser() {
        Firebase.auth.signOut()

    }

    fun updateUser(name: String?, profileUrl: String?) {
        val user = Firebase.auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse(profileUrl)
        }
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isUpdateSuccess.value = true
                }
            }
    }

    fun observeStatus(): LiveData<Boolean> {
        return isUpdateSuccess
    }

}
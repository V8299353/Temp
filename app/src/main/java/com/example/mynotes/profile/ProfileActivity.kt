package com.example.mynotes.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mynotes.R
import com.example.mynotes.auth.AuthenticationHelper
import com.example.mynotes.auth.LoginActivity
import com.example.mynotes.databinding.ActivityProfileBinding
import com.example.mynotes.helper.ProgressBarHandler
import com.example.mynotes.storage.StorageHelper
import com.google.firebase.auth.FirebaseUser


class ProfileActivity : AppCompatActivity() {

    private lateinit var activityProfileBinding: ActivityProfileBinding
    private var profileImageUri: Uri? = null
    private var currentUser: FirebaseUser? = null
    private lateinit var authHelper: AuthenticationHelper
    private lateinit var storageHelper: StorageHelper
    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(activityProfileBinding.root)

        authHelper = AuthenticationHelper()
        currentUser = authHelper.getCurrentUser()
        storageHelper = StorageHelper()
        progressBarHandler = ProgressBarHandler(this)

        activityProfileBinding.profileLogout.setOnClickListener {
            authHelper.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        activityProfileBinding.profileImage.setOnClickListener {
            selectImage()
        }

        authHelper.let {
            currentUser?.photoUrl?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .apply(RequestOptions().fitCenter())
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .error(R.drawable.ic_baseline_account_circle_24)
                    .into(activityProfileBinding.profileImage)
            }
            currentUser?.displayName?.let {
                activityProfileBinding.profileNameET.setText(it)
            }
        }

        activityProfileBinding.profileSave.setOnClickListener {
            var profileUri: String?
            progressBarHandler.show()
            profileImageUri?.let { uri ->
                storageHelper.uploadImage(uri)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageHelper.getProfileStorageReference(currentUser!!.uid)
                            .downloadUrl.addOnCompleteListener { uri ->
                                if (uri.isSuccessful) {
                                    profileUri = uri.result.toString()
                                    authHelper.updateProfile(
                                        activityProfileBinding.profileNameET.text?.toString(),
                                        profileUri
                                    )?.addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Profile Updated Successfully",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Profile Update Failed ",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    progressBarHandler.hide()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Profile Update Failed ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    progressBarHandler.hide()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Failed to save profile image", Toast.LENGTH_LONG)
                            .show()
                        progressBarHandler.hide()
                    }
                }
            } ?: run {
                authHelper.updateProfile(
                    activityProfileBinding.profileNameET.text?.toString(),
                    currentUser?.photoUrl?.toString()
                )?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Profile Updated Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Profile Update Failed ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    progressBarHandler.hide()
                }
            }
        }
    }

    private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data?.data != null) {
            Glide.with(this)
                .load(result.data!!.data)
                .apply(RequestOptions().fitCenter())
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .error(R.drawable.ic_baseline_account_circle_24)
                .into(activityProfileBinding.profileImage)
            profileImageUri = result.data!!.data
        } else {
            Toast.makeText(this, "Error loading Image", Toast.LENGTH_LONG).show()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imageLauncher.launch(Intent.createChooser(intent, "Select Image"))
    }
}
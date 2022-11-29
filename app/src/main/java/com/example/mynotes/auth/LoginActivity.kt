package com.example.mynotes.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mynotes.MainActivity
import com.example.mynotes.databinding.ActivityLoginBinding
import com.example.mynotes.helper.Helper
import com.example.mynotes.helper.ProgressBarHandler

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        progressBarHandler = ProgressBarHandler(this)

        binding.signInButton.setOnClickListener {
            with(binding) {
                if (email.text != null && password.text != null && password.text.toString().length >= 6 && Helper.isEmailValid(email.text.toString())) {
                    progressBarHandler.show()
                    AuthenticationHelper().login(email.text.toString(), password.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful) {
                            val i = Intent(this@LoginActivity, MainActivity::class.java)        // Specify any activity here e.g. home or splash or login etc
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                        } else {
                            Toast.makeText(this@LoginActivity,"Error : ${it.exception?.message}",Toast.LENGTH_LONG).show()
                        }
                        progressBarHandler.hide()
                    }
                } else if (email.text == null) {
                    email.error = "Enter Email Address"
                } else if (password.text == null) {
                    password.error = "Enter Password"
                } else if (password.text.toString().length < 6) {
                    password.error = "Password Should be of length greater than 6"
                } else if(!Helper.isEmailValid(email.text.toString())) {
                    email.error = "Enter Valid Email Address"
                }
            }
        }

        binding.signInLoginCTA.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }
    }
}
package com.example.mynotes.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mynotes.MainActivity
import com.example.mynotes.R
import com.example.mynotes.databinding.ActivityLoginBinding
import com.example.mynotes.databinding.ActivitySignUpBinding
import com.example.mynotes.helper.Helper

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            with(binding) {

                if (email.text != null && password.text != null && password.text.toString().length >= 6 && Helper.isEmailValid(email.text.toString())) {
                    AuthenticationHelper().signUn(email.text.toString(), password.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful) {
                            val i = Intent(this@SignUpActivity, MainActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(this@SignUpActivity,"Error : ${it.exception?.message}",
                                Toast.LENGTH_LONG).show()
                        }
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
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }
}
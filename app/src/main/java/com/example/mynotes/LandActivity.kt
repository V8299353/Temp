package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mynotes.auth.AuthenticationHelper
import com.example.mynotes.auth.LoginActivity
import com.example.mynotes.databinding.ActivityLandBinding

class LandActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(p0: Long) {

            }
            override fun onFinish() {
                if (AuthenticationHelper().isUserLoggedIn()) {
                    startActivity(Intent(this@LandActivity, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@LandActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
        timer.start()
    }
}
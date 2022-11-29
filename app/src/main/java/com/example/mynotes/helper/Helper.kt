package com.example.mynotes.helper

import java.text.SimpleDateFormat
import java.util.*

object Helper {

    fun isEmailValid(email:String):Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getDateString(time: Long) : String  {
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.ENGLISH)
        return simpleDateFormat.format(time)
    }
}
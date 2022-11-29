package com.example.mynotes.helper

import android.R
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout

class ProgressBarHandler(context: Context) {
    private val mProgressBar: ProgressBar
    private val mContext: Context

    init {
        mContext = context
        val layout = (context as Activity).findViewById<View>(R.id.content).rootView as ViewGroup
        mProgressBar = ProgressBar(context, null, R.attr.progressBarStyleLarge)
        mProgressBar.isIndeterminate = true
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        val rl = RelativeLayout(context)
        rl.gravity = Gravity.CENTER
        rl.addView(mProgressBar)
        layout.addView(rl, params)
        hide()
    }

    fun show() {
        mProgressBar.setVisibility(View.VISIBLE)
    }

    fun hide() {
        mProgressBar.setVisibility(View.INVISIBLE)
    }
}
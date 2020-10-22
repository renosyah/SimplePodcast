package com.renosyah.simplepodcast.ui.util

import android.content.Context
import android.view.View
import android.widget.TextView
import com.renosyah.simplepodcast.R

class LoadingLayout {
    private lateinit var c: Context
    private lateinit  var includeParent: View
    private lateinit var message: TextView

   constructor(c: Context, includeParent: View) {
        this.c = c
        this.includeParent = includeParent
        message = this.includeParent.findViewById(R.id.loading_message)
        show()
    }

    fun setMessage(m: String) {
        message.text = m
    }

    fun setVisibility(v: Boolean) {
        includeParent.visibility = (if (v) View.VISIBLE else View.GONE)
    }

    fun show() {
        includeParent.visibility = (View.VISIBLE)
    }

    fun hide() {
        includeParent.visibility = (View.GONE)
    }
}
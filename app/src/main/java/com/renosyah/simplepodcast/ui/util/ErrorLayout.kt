package com.renosyah.simplepodcast.ui.util

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.renosyah.simplepodcast.R

class ErrorLayout {
    private lateinit var c: Context
    private lateinit  var includeParent: View
    private lateinit var message: TextView
    private lateinit var ok :Button

   constructor(c: Context, includeParent: View, onclick : ()->Unit) {
        this.c = c
        this.includeParent = includeParent
        message = this.includeParent.findViewById(R.id.error_message_text)
        ok = this.includeParent.findViewById(R.id.button_try_again)
        ok.setOnClickListener{
            this.hide()
            onclick.invoke()
        }
        show()
    }

    fun setOnclick(onclick : ()->Unit){
        ok.setOnClickListener{
            this.hide()
            onclick.invoke()
        }
    }

    fun setMessage(m: String?) {
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
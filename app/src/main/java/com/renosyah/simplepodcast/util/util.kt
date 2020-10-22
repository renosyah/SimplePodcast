package com.renosyah.simplepodcast.util

import android.app.ActivityManager
import android.content.Context
import com.renosyah.simplepodcast.model.ResponseError


class util {
    companion object {
        fun errorToString(errors : ArrayList<ResponseError>) : String {
            var m = ""
            for (e in errors){
                m += e.message + " "
            }
            return m
        }

        fun isMyServiceRunning(c: Context, s: Class<*>): Boolean {
            val manager =
                (c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (s.name == service.service.className) {
                    return true
                }
            }
            return false
        }
    }
}
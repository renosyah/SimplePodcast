package com.renosyah.simplepodcast.util

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.renosyah.simplepodcast.model.ResponseError
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception


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
        fun getBitmapWithPicasso(url : String,onLoad : (Bitmap)-> Unit){
            val target = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    if (bitmap != null)
                        onLoad.invoke(bitmap)
                }
            }
            Picasso.get().load(url).into(target)
        }

    }
}
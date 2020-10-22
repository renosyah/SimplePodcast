package com.renosyah.simplepodcast.util

import android.content.Context
import com.renosyah.simplepodcast.BuildConfig
import java.io.*

class SerializableSave {

    companion object {
        val userDataFileSessionName = "${BuildConfig.APPLICATION_ID}_userSessionData.data"
        val lastMusicData = "${BuildConfig.APPLICATION_ID}_last_music.data"
    }

    lateinit var context: Context
    lateinit var  filename: String

    constructor(context: Context, filename: String) {
        this.context = context
        this.filename = filename
    }


    fun save(d: Serializable): Boolean {
        var save: Boolean = false
        try {

            val fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val os = ObjectOutputStream(fos)
            os.writeObject(d)
            os.close()
            fos.close()

            save = true

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return save

    }

    fun load(): Serializable? {
        var data: Serializable? = null

        try {
            val fis = context.openFileInput(filename)
            val file = ObjectInputStream(fis)
            data = file.readObject() as Serializable
            file.close()
            fis.close()

        } catch (e: IOException) {
            e.printStackTrace()

        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return data
    }

    fun delete(): Boolean {
        val f = File(context.getFilesDir(), userDataFileSessionName)
        return f.deleteRecursively()
    }
}
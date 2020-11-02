package com.renosyah.simplepodcast.model.category

import com.google.gson.annotations.SerializedName
import com.renosyah.simplepodcast.model.BaseModel
import com.renosyah.simplepodcast.model.music.Music

class Category(

    @SerializedName("id")
    var id : String = "",

    @SerializedName("name")
    var name : String = "",

    @SerializedName("image_url")
    var imageUrl : String = "",


    var musics: ArrayList<Music> = ArrayList()

) : BaseModel {

    fun clone() : Category {
        return Category(
            this.id,
            this.name,
            this.imageUrl
        )
    }
}
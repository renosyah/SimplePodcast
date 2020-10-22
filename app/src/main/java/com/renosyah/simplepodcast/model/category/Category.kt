package com.renosyah.simplepodcast.model.category

import com.google.gson.annotations.SerializedName
import com.renosyah.simplepodcast.model.BaseModel

class Category(

    @SerializedName("id")
    var id : String = "",

    @SerializedName("name")
    var name : String = "",

    @SerializedName("image_url")
    var imageUrl : String = ""


) : BaseModel {

    fun clone() : Category {
        return Category(
            this.id,
            this.name,
            this.imageUrl
        )
    }
}
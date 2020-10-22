package com.renosyah.simplepodcast.model.music

import com.google.gson.annotations.SerializedName
import com.renosyah.simplepodcast.BuildConfig
import com.renosyah.simplepodcast.model.BaseModel

class Music(

    @SerializedName("id")
    var id : String = "",

    @SerializedName("category_id")
    var categoryId : String = "",

    @SerializedName("title")
    var title : String = "",

    @SerializedName("description")
    var description  : String = "",

    @SerializedName("image_cover_url")
    var imageCoverUrl  : String = "",


    var seekPos : Int = 0,


    var duration : Int = 0

) : BaseModel {

    fun clone() : Music {
        return Music(
            this.id,
            this.categoryId,
            this.title,
            this.description,
            this.imageCoverUrl
        )
    }

    fun getUrl() : String {
        return "${BuildConfig.SERVER_URL}/api/v1/musics-file/${this.id}"
    }
}
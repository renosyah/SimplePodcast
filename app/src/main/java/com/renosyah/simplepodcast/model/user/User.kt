package com.renosyah.simplepodcast.model.user

import com.google.gson.annotations.SerializedName
import com.renosyah.simplepodcast.model.BaseModel

class User(

    @SerializedName("id")
    var id : String = "",

    @SerializedName("name")
    var name : String = "",

    @SerializedName("phone_number")
    var phoneNumber : String = "",

    @SerializedName("password")
    var password : String = ""

) : BaseModel {

    fun clone() : User {
        return User(
            this.id,
            this.name,
            this.phoneNumber,
            this.password
        )
    }
}
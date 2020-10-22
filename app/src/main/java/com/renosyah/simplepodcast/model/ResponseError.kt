package com.renosyah.simplepodcast.model

import com.google.gson.annotations.SerializedName

class ResponseError (
    @SerializedName("log") var log : String,
    @SerializedName("status") var status : Int,
    @SerializedName("message") var message : String
)
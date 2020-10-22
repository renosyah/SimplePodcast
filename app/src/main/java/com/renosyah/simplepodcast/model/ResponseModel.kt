package com.renosyah.simplepodcast.model

import com.google.gson.annotations.SerializedName


class ResponseModel<T>(
    @SerializedName("data") var data: T? = null,
    @SerializedName("errors") var errors: ArrayList<ResponseError> = ArrayList(),
    @SerializedName("status") var status : Int = 200
)
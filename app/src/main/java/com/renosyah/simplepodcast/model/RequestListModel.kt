package com.renosyah.simplepodcast.model

import com.google.gson.annotations.SerializedName

class RequestListModel(
    @SerializedName("filter_by")
    var filterBy: String = "flag_status",

    @SerializedName("filter_value")
    var filterValue: String = "0",

    @SerializedName("search_by")
    var searchBy: String = "",

    @SerializedName("search_value")
    var searchValue: String = "",

    @SerializedName("order_by")
    var orderBy: String = "",

    @SerializedName("order_dir")
    var orderDir: String = "",

    @SerializedName("offset")
    var offset: Int = 0,

    @SerializedName("limit")
    var limit: Int = 0

) : BaseModel {
    fun clone() : RequestListModel {
        return RequestListModel(
            this.filterBy,
            this.filterValue,
            this.searchBy,
            this.searchValue,
            this.orderBy,
            this.orderDir,
            this.offset,
            this.limit
        )
    }
}


package com.renosyah.simplepodcast.service

import com.renosyah.simplepodcast.BuildConfig
import com.renosyah.simplepodcast.model.RequestListModel
import com.renosyah.simplepodcast.model.ResponseModel
import com.renosyah.simplepodcast.model.music.Music
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitService {

    // add more end point to access
    @POST("api/v1/musics-list")
    fun allMusic(@Header("session") sessionId :String, @Body req : RequestListModel): Observable<ResponseModel<ArrayList<Music>>>

    companion object {
        fun create() : RetrofitService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.SERVER_URL)
                .build()
            return retrofit.create(RetrofitService::class.java)
        }
    }
}
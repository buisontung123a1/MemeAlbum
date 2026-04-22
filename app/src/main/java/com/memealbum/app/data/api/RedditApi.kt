package com.memealbum.app.data.api

import com.memealbum.app.data.model.RedditResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApi {

    @GET("r/memes/hot.json")
    suspend fun getHotMemes(
        @Query("limit") limit: Int = 50,
        @Query("after") after: String? = null,
        @Query("raw_json") rawJson: Int = 1
    ): RedditResponse

    @GET("r/memes/new.json")
    suspend fun getNewMemes(
        @Query("limit") limit: Int = 50,
        @Query("after") after: String? = null,
        @Query("raw_json") rawJson: Int = 1
    ): RedditResponse

    @GET("r/memes/top.json")
    suspend fun getTopMemes(
        @Query("limit") limit: Int = 50,
        @Query("t") time: String = "day",
        @Query("after") after: String? = null,
        @Query("raw_json") rawJson: Int = 1
    ): RedditResponse
}

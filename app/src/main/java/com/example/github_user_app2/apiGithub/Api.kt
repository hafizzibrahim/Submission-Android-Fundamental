package com.example.github_user_app2.apiGithub

import com.example.github_user_app2.data.model.DetailUserResponse
import com.example.github_user_app2.data.model.User
import com.example.github_user_app2.data.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("search/users")
    @Headers("Authorization: token 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")
    fun getSearchUsers(@Query("q") query: String): Call<UserResponse>

    @GET("users")
    suspend fun getTopUsers(
        @Query("per_page") perPage: Int,
        @Query("since") sinceUserId: Int
    ): List<User>

    @GET("users/{username}")
    @Headers("Authorization: token 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")
    fun getUserDetail(
        @Path("username") username: String
    ):Call<DetailUserResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: token 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")
    fun getFollowers(
        @Path("username") username: String
    ):Call<ArrayList<User>>

    @GET("users/{username}/following")
    @Headers("Authorization: token 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")
    fun getFollowing(
        @Path("username") username: String
    ): Call<ArrayList<User>>
}
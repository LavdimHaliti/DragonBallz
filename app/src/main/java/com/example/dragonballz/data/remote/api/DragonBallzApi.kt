package com.example.dragonballz.data.remote.api

import com.example.dragonballz.data.remote.dto.CharacterDto
import com.example.dragonballz.data.remote.response.CharacterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DragonBallzApi {

    @GET("characters")
    suspend fun searchCharacter(
        @Query("name") name: String
    ): Response<List<CharacterDto>>

    @GET("characters")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<CharacterResponse>

    @GET("characters/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): Response<CharacterDto>

    @GET("planets/{id}")
    suspend fun getPlanet(
        @Path("id") id: Int
    ): Response<CharacterDto>
}
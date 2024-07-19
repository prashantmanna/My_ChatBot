package com.example.mychatbot.ImageGeneration.api

import com.example.mychatbot.ImageGeneration.ImageModel.ImageRequestBody
import com.example.mychatbot.ImageGeneration.ImageModel.generatedImage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers(
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZjE5YjIxNDctY2U0ZC00ZWJlLWEwMDktYWQ5ZmYzMjA5ZjkwIiwidHlwZSI6ImFwaV90b2tlbiJ9.xmJnFA5VUpthfbrIynGrjo01VvvBx2POaTFpCOJK_2g",
        "Content-Type: application/json"
    )
    @POST("image/generation")
    fun generateImage(
        @Body request : ImageRequestBody
    ) : Call<generatedImage>
}
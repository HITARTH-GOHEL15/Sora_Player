package com.example.soraplayer.ExploreScreen

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface PexelsApiService {

    @Headers("Authorization: BMzeXsEuTdSdmcEHrnRwasrR1gzhTp5k4FHZVyBRsRCxMDJ143MVS1p0") // Replace with your API key
    @GET("v1/search")
    suspend fun getPhotos(
        @Query("query") query: String,   // Search keyword for photos
        @Query("per_page") perPage: Int = 1000  // Number of results per page
    ): PexelsResponse
}


data class PexelsResponse(
    val total_results: Int,
    val photos: List<PexelsPhoto>
)

data class PexelsPhoto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val src: PexelsPhotoSrc
)

data class PexelsPhotoSrc(
    val original: String,
    val small: String // Use "small" for thumbnails in the grid
)

object RetrofitInstance {
    private const val BASE_URL = "https://api.pexels.com/"

    val api: PexelsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PexelsApiService::class.java)
    }
}




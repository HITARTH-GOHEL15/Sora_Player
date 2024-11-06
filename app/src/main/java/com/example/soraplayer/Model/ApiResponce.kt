package com.example.soraplayer.Model

import com.google.gson.annotations.SerializedName

data class UnsplashPhoto(
    val id: String,
    val urls: PhotoUrls,
    val links: PhotoLinks
)

data class PhotoUrls(
    val small: String,  // Small image URL
)

data class PhotoLinks(
    val html: String  // Link to Unsplash image page
)


package com.example.soraplayer.Data

data class MediaFile(
    val id: Long,
    val name: String,
    val uri: String,
    val type: MediaType,
    val size: Long,
    val dateModified: Long,
    val FolderId: Long,
    val duration: Long? = null
)

enum class MediaType {
    VIDEO,
    MUSIC,
    IMAGE,
    OTHER

}
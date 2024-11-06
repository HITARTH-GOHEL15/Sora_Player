package com.example.soraplayer.ExploreScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class ExploreItem(
    val imageUrl: String,
    val linkUrl: String
)

class ExploreViewModel : ViewModel() {

    private val _exploreItems = MutableStateFlow<List<ExploreItem>>(emptyList())
    val exploreItems: StateFlow<List<ExploreItem>> = _exploreItems

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        fetchImages("nature") // Default query to fetch nature images
    }

    fun fetchImages(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.api.getPhotos(query, perPage = 1000)
                _exploreItems.value = response.photos.map { photo ->
                    ExploreItem(
                        imageUrl = photo.src.small,
                        linkUrl = photo.url
                    )
                }
            } catch (e: IOException) {
                _errorMessage.value = "Network Error: ${e.message}"
            } catch (e: HttpException) {
                _errorMessage.value = "Error fetching photos: ${e.message()}"
            } finally {
                _loading.value = false
            }
        }
    }
}

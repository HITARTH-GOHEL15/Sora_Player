import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DownloadItem(
    val id: Long,
    val url: String,
    var status: Int,
    var progress: Int,
    val localUri: String? = null,
    var pausedAtBytes: Int = 0
)

class DownloadViewModel(
    private val context: Context
) : ViewModel() {

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads = _downloads.asStateFlow()  // Exposed as StateFlow

    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }
    private val pausedDownloads = mutableMapOf<Long, DownloadItem>()  // To store paused downloads

    fun addDownload(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url.substringAfterLast('/'))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)

        val downloadId = downloadManager.enqueue(request)

        val newDownload = DownloadItem(
            id = downloadId,
            url = url,
            status = DownloadManager.STATUS_PENDING,
            progress = 0
        )

        _downloads.value = _downloads.value + newDownload

        // Start monitoring progress
        viewModelScope.launch {
            monitorDownloadProgress(downloadId)
        }
    }

    private suspend fun monitorDownloadProgress(downloadId: Long) {
        var isDownloading = true
        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor: Cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                val progress = if (totalBytes > 0) {
                    (bytesDownloaded * 100L / totalBytes).toInt()
                } else 0

                // Update the status and progress of the download
                _downloads.value = _downloads.value.map {
                    if (it.id == downloadId) {
                        it.copy(status = status, progress = progress)
                    } else it
                }

                isDownloading = status == DownloadManager.STATUS_RUNNING || status == DownloadManager.STATUS_PENDING
            }
            cursor.close()

            // Add a small delay to avoid excessive querying
            delay(1000)
        }
    }



    fun pauseDownload(id: Long) {
        // Note: DownloadManager doesn't provide an easy way to pause/resume.
        // Implement custom logic if required (e.g., cancel + re-download).
        // For simplicity, we'll just delete the download.
        val download = _downloads.value.find { it.id == id }
        if (download != null) {
            downloadManager.remove(id)  // This cancels the download
            pausedDownloads[id] = download // Store the download information in the pausedDownloads map
            _downloads.value = _downloads.value.map { if (it.id == id) it.copy(status = DownloadManager.STATUS_PAUSED) else it }
        }

    }

    fun deleteDownload(id: Long) {
        downloadManager.remove(id)
        _downloads.value = _downloads.value.filterNot { it.id == id }
        pausedDownloads.remove(id)
    }



    fun resumeDownload(id: Long) {
        // Retrieve the paused download info and re-enqueue the download
        val pausedDownload = pausedDownloads[id]
        if (pausedDownload != null) {
            val request = DownloadManager.Request(Uri.parse(pausedDownload.url))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pausedDownload.url.substringAfterLast('/'))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)

            val newDownloadId = downloadManager.enqueue(request)

            // Update the new download ID in the list
            _downloads.value = _downloads.value.map {
                if (it.id == id) {
                    it.copy(id = newDownloadId, status = DownloadManager.STATUS_PENDING, progress = 0) // Reset progress
                } else it
            }

            // Remove from pausedDownloads
            pausedDownloads.remove(id)

            // Start monitoring the new download
            viewModelScope.launch {
                monitorDownloadProgress(newDownloadId)
            }
        }

    }
}



class DownloadViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
            return DownloadViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

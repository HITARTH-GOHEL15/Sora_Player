package com.example.soraplayer.MinimizePlayer

// LastPlayedPreferenceHelper.kt

import android.content.Context
import android.net.Uri
import com.example.soraplayer.Model.MinimizedPlayerState

object LastPlayedPreferenceHelper {
    private const val PREFS_NAME = "LastPlayedPrefs"
    private const val LAST_PLAYED_URI_KEY = "lastPlayedUri"
    private const val TITLE_KEY = "title"
    private const val IS_MUSIC_KEY = "isMusic"

    fun saveLastPlayedItem(context: Context, uri: Uri, title: String, isMusic: Boolean) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString(LAST_PLAYED_URI_KEY, uri.toString())
            .putString(TITLE_KEY, title)
            .putBoolean(IS_MUSIC_KEY, isMusic)
            .apply()
    }

    fun getLastPlayedItem(context: Context): MinimizedPlayerState {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uri = sharedPrefs.getString(LAST_PLAYED_URI_KEY, null)?.let { Uri.parse(it) }
        val title = sharedPrefs.getString(TITLE_KEY, "") ?: ""
        val isMusic = sharedPrefs.getBoolean(IS_MUSIC_KEY, true)

        return MinimizedPlayerState(
            lastPlayedUri = uri,
            title = title,
            isMusic = isMusic
        )
    }
}

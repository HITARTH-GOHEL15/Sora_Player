package com.example.soraplayer.setting_screen

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(context: Context) : ViewModel() {

    // Language options
    val languages = listOf("English", "Spanish", "French")

    // State variables for settings
    private val _selectedLanguage = MutableStateFlow("English")  // Default language
    val selectedLanguage = _selectedLanguage.asStateFlow()

    private val _isDarkModeEnabled = MutableStateFlow(false)  // Default to Light Mode
    val isDarkModeEnabled = _isDarkModeEnabled.asStateFlow()

    private val _clipboardPromptEnabled = MutableStateFlow(true)  // Default to enabled
    val clipboardPromptEnabled = _clipboardPromptEnabled.asStateFlow()

    // Function to change language
    fun changeLanguage(context: Context, languageCode: String, activity: Activity) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Persist language change if necessary

        // Restart activity to apply the new language
        activity.recreate()
    }


    // Function to toggle dark mode
    fun toggleDarkMode(enabled: Boolean) {
        _isDarkModeEnabled.value = enabled
    }

    fun updateAppLanguage(context: Context, language: String) {
        val locale = when (language) {
            "Spanish" -> Locale("es")
            "French" -> Locale("fr")
            else -> Locale("en")
        }

        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    // Function to toggle clipboard prompt
    fun toggleClipboardPrompt(enabled: Boolean) {
        _clipboardPromptEnabled.value = enabled
    }
}

class SettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




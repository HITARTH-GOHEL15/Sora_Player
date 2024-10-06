package com.example.soraplayer.setting_screen


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    // State for Language Selection
    private val _language = MutableStateFlow("English") // Default language
    val language = _language.asStateFlow()

    // State for Dark Mode
    private val _isDarkMode = MutableStateFlow(false) // Default is light mode
    val isDarkMode = _isDarkMode.asStateFlow()

    // State for Clipboard Prompt
    private val _isClipboardPromptEnabled = MutableStateFlow(true) // Default is enabled
    val isClipboardPromptEnabled = _isClipboardPromptEnabled.asStateFlow()

    // Functions to change settings
    fun changeLanguage(newLanguage: String) {
        _language.value = newLanguage
        // Save to persistent storage if necessary (e.g., DataStore/SharedPreferences)
    }

    fun toggleDarkMode(isEnabled: Boolean) {
        _isDarkMode.value = isEnabled
        // Save to persistent storage if necessary
    }

    fun toggleClipboardPrompt(isEnabled: Boolean) {
        _isClipboardPromptEnabled.value = isEnabled
        // Save to persistent storage if necessary
    }
}

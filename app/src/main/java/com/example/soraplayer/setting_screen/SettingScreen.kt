package com.example.soraplayer.setting_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val language by settingsViewModel.language.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val isClipboardPromptEnabled by settingsViewModel.isClipboardPromptEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Language Selection
        Text(text = "Language", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LanguageDropdown(
            selectedLanguage = language,
            onLanguageChange = { settingsViewModel.changeLanguage(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dark Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isDarkMode,
                onCheckedChange = { settingsViewModel.toggleDarkMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clipboard Prompt Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Clipboard Prompt", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isClipboardPromptEnabled,
                onCheckedChange = { settingsViewModel.toggleClipboardPrompt(it) }
            )
        }
    }
}


@Composable
fun LanguageDropdown(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val languages = listOf("English", "Spanish", "French", "German", "Chinese")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedLanguage)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onLanguageChange(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

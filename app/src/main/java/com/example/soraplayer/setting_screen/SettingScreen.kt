package com.example.soraplayer.setting_screen

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(activity: Activity, context: Context, viewModel: SettingsViewModel = viewModel()) {
    // Collect state from the ViewModel
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val isDarkModeEnabled by viewModel.isDarkModeEnabled.collectAsState()
    val clipboardPromptEnabled by viewModel.clipboardPromptEnabled.collectAsState()

    val languages = listOf("en", "es", "fr", "de")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown for language selection
        Text("Select Language")
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selectedLanguage)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                viewModel.languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(text = language) }, // Corrected here
                        onClick = {
                            viewModel.changeLanguage(context , language, activity)  // Pass context here
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle for dark mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Mode")
            Switch(
                checked = isDarkModeEnabled,
                onCheckedChange = { viewModel.toggleDarkMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle for clipboard prompt
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Clipboard Prompt")
            Switch(
                checked = clipboardPromptEnabled,
                onCheckedChange = { viewModel.toggleClipboardPrompt(it) }
            )
        }
    }
}

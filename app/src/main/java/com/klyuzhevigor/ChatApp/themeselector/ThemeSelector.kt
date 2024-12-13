package com.klyuzhevigor.ChatApp.themeselector

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

class ThemePreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    var isDarkTheme: Boolean?
        get() {
            return if (sharedPreferences.contains("is_dark_theme")) {
                sharedPreferences.getBoolean("is_dark_theme", false)
            } else {
                null
            }
        }
        set(value) {
            if (value != null) {
                sharedPreferences.edit().putBoolean("is_dark_theme", value).apply()
            } else {
                sharedPreferences.edit().remove("is_dark_theme").apply()
            }
        }

    var themeOption: ThemeOption
        get() {
            if (isDarkTheme != null) {
                return if (isDarkTheme == true) ThemeOption.DARK else ThemeOption.LIGHT
            } else {
                return ThemeOption.SYSTEM
            }
        }
        set(v) {
            isDarkTheme = if (v == ThemeOption.SYSTEM) {
                null
            } else {
                v == ThemeOption.DARK
            }
        }
}


@Composable
fun ThemeSelectionScreen(preferences: ThemePreferences, action: (ThemeOption) -> Unit) {
    var selectedTheme by remember { mutableStateOf(preferences.themeOption) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Выберите тему", style = MaterialTheme.typography.headlineLarge)

        ThemeOption.values().forEach { themeOption ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedTheme == themeOption),
                    onClick = {
                        selectedTheme = themeOption
                        preferences.themeOption = themeOption
                        action(selectedTheme)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(themeOption.name)
            }
        }
    }
}

enum class ThemeOption {
    LIGHT, DARK, SYSTEM
}

@Preview(showBackground = true)
@Composable
fun PreviewThemeSelectionScreen() {
    ThemeSelectionScreen(ThemePreferences(LocalContext.current)) {}
}

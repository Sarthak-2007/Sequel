package com.personalplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.personalplanner.ui.screens.WeeklyPlannerScreen // Will be created in a later step
import com.personalplanner.ui.theme.PersonalPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalPlannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // WeeklyPlannerScreen will be the main content.
                    // For now, it's commented out as it doesn't exist yet.
                    // Once WeeklyPlannerScreen is implemented, uncomment the line below.
                    WeeklyPlannerScreen()
                }
            }
        }
    }
}

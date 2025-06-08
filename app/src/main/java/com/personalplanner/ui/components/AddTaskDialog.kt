package com.personalplanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.personalplanner.ui.theme.PersonalPlannerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onTaskAdd: (title: String) -> Unit,
    selectedDate: LocalDate
) {
    if (!showDialog) return

    var taskTitle by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = {
            taskTitle = "" // Reset title on dismiss
            onDismiss()
        },
        title = {
            Column {
                Text("Add New Task")
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        text = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onTaskAdd(taskTitle)
                        taskTitle = "" // Reset title
                        onDismiss() // Dismiss dialog after adding
                    }
                },
                enabled = taskTitle.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                taskTitle = "" // Reset title
                onDismiss()
            }) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false) // Prevent dismissal by clicking outside
    )

    // Request focus when dialog becomes visible
    LaunchedEffect(showDialog) {
        if (showDialog) {
            // Small delay to ensure dialog is composed and focusable
            kotlinx.coroutines.delay(100)
            focusRequester.requestFocus()
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewAddTaskDialog() {
    PersonalPlannerTheme {
        AddTaskDialog(
            showDialog = true,
            onDismiss = {},
            onTaskAdd = { _ -> },
            selectedDate = LocalDate.now()
        )
    }
}

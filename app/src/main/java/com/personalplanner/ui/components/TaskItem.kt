package com.personalplanner.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalplanner.data.database.TaskEntity
import com.personalplanner.ui.theme.PersonalPlannerTheme
import com.personalplanner.ui.theme.Orange // Specified for completed tasks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: TaskEntity,
    onToggleComplete: (Long) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                onDeleteTask(task)
                true // Confirm the dismiss action
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart), // Allow swipe from both directions
        background = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToStart -> MaterialTheme.colorScheme.errorContainer
                    DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent // Or a subtle background
                }, label = "DismissBackground"
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f, label = "DismissIconScale"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    modifier = Modifier.scale(scale),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        dismissContent = {
            TaskItemContent(
                task = task,
                onToggleComplete = onToggleComplete
            )
        }
    )
}

@Composable
fun TaskItemContent(
    task: TaskEntity,
    onToggleComplete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp), // Small vertical padding between items
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete(task.id) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Orange, // Primary color for completed tasks (as per spec)
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    checkmarkColor = MaterialTheme.colorScheme.surface // White checkmark on Orange
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = task.title,
                style = if (task.isCompleted) {
                    MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                modifier = Modifier.weight(1f)
            )
            // Optional: Explicit delete icon if swipe isn't preferred or as an alternative
            // IconButton(onClick = { onDeleteTask(task) }) {
            //     Icon(Icons.Default.Delete, contentDescription = "Delete Task")
            // }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskItemCompleted() {
    PersonalPlannerTheme {
        TaskItemContent(
            task = TaskEntity(id = 1, title = "Completed Task", date = "2023-03-15", isCompleted = true, createdAt = 0L, orderIndex = 0),
            onToggleComplete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskItemIncomplete() {
    PersonalPlannerTheme {
        TaskItemContent(
            task = TaskEntity(id = 2, title = "Incomplete Task", date = "2023-03-15", isCompleted = false, createdAt = 0L, orderIndex = 0),
            onToggleComplete = {}
        )
    }
}

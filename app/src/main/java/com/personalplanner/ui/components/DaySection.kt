package com.personalplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personalplanner.data.database.TaskEntity
import com.personalplanner.ui.theme.PersonalPlannerTheme
import com.personalplanner.ui.theme.Orange // For highlighting current day
import com.personalplanner.utils.DateUtils // Assuming DateUtils is injectable or accessible
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DaySection(
    date: LocalDate,
    tasks: List<TaskEntity>,
    onToggleComplete: (Long) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    isCurrentDay: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    dateUtils: DateUtils // Pass DateUtils for formatting
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // Spacing between day sections
    ) {
        // Header for the Day
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isCurrentDay) Orange.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                .clickable { onDateSelected(date) } // Allow selecting the day
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateUtils.getDayDisplayName(date, TextStyle.FULL, Locale.getDefault()),
                style = MaterialTheme.typography.headlineSmall, // Mapped to DayHeaderStyle (Bold, 24sp)
                color = if (isCurrentDay) Orange else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Normal), // Date number, less bold
                color = if (isCurrentDay) Orange else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of Tasks for the Day
        if (tasks.isEmpty()) {
            Text(
                text = "No tasks for this day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            // Using Column for now as LazyColumn inside another LazyColumn (WeeklyPlannerScreen) can be problematic
            // If performance becomes an issue with many tasks per day, this might need rethinking,
            // or the outer screen needs to be structured differently (e.g. not LazyColumn of DaySections).
            // For a typical weekly planner, the number of tasks per day might be manageable in a simple Column.
            Column(modifier = Modifier.padding(horizontal = 8.dp)) { // Horizontal padding for task items
                tasks.forEach { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = onToggleComplete,
                        onDeleteTask = onDeleteTask,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDaySectionWithTasks() {
    val sampleDate = LocalDate.now()
    val sampleTasks = listOf(
        TaskEntity(id = 1, title = "Morning Jog", date = sampleDate.toString(), isCompleted = true),
        TaskEntity(id = 2, title = "Team Meeting", date = sampleDate.toString()),
        TaskEntity(id = 3, title = "Grocery Shopping", date = sampleDate.toString())
    )
    PersonalPlannerTheme {
        DaySection(
            date = sampleDate,
            tasks = sampleTasks,
            onToggleComplete = {},
            onDeleteTask = {},
            isCurrentDay = true,
            onDateSelected = {},
            dateUtils = DateUtils() // For preview
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDaySectionNoTasks() {
    val sampleDate = LocalDate.now().plusDays(1)
    PersonalPlannerTheme {
        DaySection(
            date = sampleDate,
            tasks = emptyList(),
            onToggleComplete = {},
            onDeleteTask = {},
            isCurrentDay = false,
            onDateSelected = {},
            dateUtils = DateUtils() // For preview
        )
    }
}

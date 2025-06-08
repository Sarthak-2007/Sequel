package com.personalplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.personalplanner.data.database.TaskEntity
import com.personalplanner.ui.components.AddTaskDialog
import com.personalplanner.ui.components.DaySection
import com.personalplanner.ui.theme.PersonalPlannerTheme
import com.personalplanner.utils.DateUtils
import com.personalplanner.viewmodel.TaskViewModel
import com.personalplanner.viewmodel.WeeklyPlannerUiState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlannerScreen(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val dateUtils = remember { DateUtils() } // Or inject if preferred at this level

    Scaffold(
        topBar = {
            PlannerTopAppBar(
                weekHeader = uiState.currentWeekHeader,
                onPreviousWeek = { viewModel.goToPreviousWeek() },
                onNextWeek = { viewModel.goToNextWeek() },
                onToday = { viewModel.goToToday() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (uiState.weekDays.isEmpty()) {
                    Text(
                        "No dates to display.",
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.weekDays, key = { it.toString() }) { day ->
                            val tasksForDay = uiState.tasksByDate[day] ?: emptyList()
                            DaySection(
                                date = day,
                                tasks = tasksForDay,
                                onToggleComplete = { taskId -> viewModel.toggleTaskCompletion(taskId) },
                                onDeleteTask = { task -> viewModel.deleteTask(task) },
                                isCurrentDay = dateUtils.isSameDay(day, dateUtils.getCurrentDate()),
                                onDateSelected = { selectedDate -> viewModel.selectDate(selectedDate) },
                                dateUtils = dateUtils,
                                modifier = Modifier.padding(horizontal = 16.dp) // Screen margins
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            showDialog = true,
            onDismiss = { showAddTaskDialog = false },
            onTaskAdd = { title ->
                viewModel.addTask(title, uiState.selectedDate)
                showAddTaskDialog = false
            },
            selectedDate = uiState.selectedDate
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerTopAppBar(
    weekHeader: String,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onToday: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = weekHeader,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onPreviousWeek) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Previous Week")
            }
        },
        actions = {
            TextButton(onClick = onToday) {
                Text("Today")
            }
            IconButton(onClick = onNextWeek) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Next Week")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

// --- Preview Setup ---

// Mock ViewModel for Preview
class PreviewTaskViewModel : TaskViewModel(PreviewTaskRepository(), DateUtils()) {
    // You can override methods or flows if needed for more complex previews
}

// Mock Repository for Preview
class PreviewTaskRepository : TaskRepository(PreviewTaskDao()) {
    override fun getTasksForWeek(startDate: String, endDate: String): kotlinx.coroutines.flow.Flow<List<TaskEntity>> {
        val tasks = mutableListOf<TaskEntity>()
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        var current = start
        while (!current.isAfter(end)) {
            if (current.dayOfWeek.value <= 5) { // Add tasks only for weekdays for preview variety
                 tasks.add(TaskEntity(id = (tasks.size + 1).toLong(), title = "Task ${tasks.size + 1} for $current", date = current.toString(), isCompleted = tasks.size % 2 == 0))
                 tasks.add(TaskEntity(id = (tasks.size + 1).toLong(), title = "Task ${tasks.size + 1} for $current", date = current.toString()))
            }
            current = current.plusDays(1)
        }
        return kotlinx.coroutines.flow.flowOf(tasks)
    }
}

// Mock DAO for Preview
class PreviewTaskDao : com.personalplanner.data.database.TaskDao {
    override fun getTasksForDate(date: String): kotlinx.coroutines.flow.Flow<List<TaskEntity>> = kotlinx.coroutines.flow.flowOf(emptyList())
    override fun getTasksForWeek(startDate: String, endDate: String): kotlinx.coroutines.flow.Flow<List<TaskEntity>> = kotlinx.coroutines.flow.flowOf(emptyList()) // Handled by Repo override
    override suspend fun insertTask(task: TaskEntity): Long = 0L
    override suspend fun updateTask(task: TaskEntity) {}
    override suspend fun deleteTask(task: TaskEntity) {}
    override suspend fun toggleTaskCompletion(taskId: Long) {}
    override suspend fun getMaxOrderIndexForDate(date: String): Int? = 0
    override fun getTaskById(taskId: Long): kotlinx.coroutines.flow.Flow<TaskEntity?> = kotlinx.coroutines.flow.flowOf(null)
}


@Preview(showSystemUi = true, name = "Weekly Planner Screen Preview")
@Composable
fun WeeklyPlannerScreenPreview() {
    PersonalPlannerTheme {
        // This preview will use the actual ViewModel with Hilt in an environment where Hilt works (like device/emulator).
        // For Studio previews that don't fully support Hilt, it might be limited or require the PreviewTaskViewModel.
        // For simplicity here, we assume a context where hiltViewModel() can provide a basic instance or mock.
        // If issues, pass PreviewTaskViewModel explicitly: WeeklyPlannerScreen(viewModel = PreviewTaskViewModel())
        WeeklyPlannerScreen(viewModel = PreviewTaskViewModel())
    }
}

@Preview(showBackground = true, name = "Top App Bar Preview")
@Composable
fun PlannerTopAppBarPreview() {
    PersonalPlannerTheme {
        PlannerTopAppBar(
            weekHeader = "March 2024",
            onPreviousWeek = { },
            onNextWeek = { },
            onToday = { }
        )
    }
}

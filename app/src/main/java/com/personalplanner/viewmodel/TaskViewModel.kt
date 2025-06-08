package com.personalplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personalplanner.data.database.TaskEntity
import com.personalplanner.data.repository.TaskRepository
import com.personalplanner.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class WeeklyPlannerUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekDays: List<LocalDate> = emptyList(),
    val tasksByDate: Map<LocalDate, List<TaskEntity>> = emptyMap(),
    val isLoading: Boolean = true,
    val currentWeekHeader: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dateUtils: DateUtils
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyPlannerUiState(selectedDate = dateUtils.getCurrentDate()))
    val uiState: StateFlow<WeeklyPlannerUiState> = _uiState.asStateFlow()

    // Separate flow for the reference date of the current week (Monday of the selected week)
    private val currentWeekMonday: MutableStateFlow<LocalDate> = MutableStateFlow(
        dateUtils.getWeekDays(dateUtils.getCurrentDate()).firstOrNull() ?: dateUtils.getCurrentDate()
    )

    init {
        // Initialize with current date and week
        val initialSelectedDate = dateUtils.getCurrentDate()
        _uiState.update {
            it.copy(
                selectedDate = initialSelectedDate,
                weekDays = dateUtils.getWeekDays(initialSelectedDate),
                currentWeekHeader = generateWeekHeader(initialSelectedDate)
            )
        }
        currentWeekMonday.value = dateUtils.getWeekDays(initialSelectedDate).firstOrNull() ?: initialSelectedDate


        // Observe changes in currentWeekMonday and fetch tasks for that week
        currentWeekMonday.flatMapLatest { monday ->
            val weekDays = dateUtils.getWeekDays(monday)
            val startDateStr = dateUtils.toIsoDateString(weekDays.first())
            val endDateStr = dateUtils.toIsoDateString(weekDays.last())

            // Create a list of flows, one for each day's tasks
            // This was getting too complex with combining many flows, simplifying to one weekly fetch
            taskRepository.getTasksForWeek(startDateStr, endDateStr)
        }.onEach { weeklyTasks ->
            // Group tasks by date
            val tasksGroupedByDate = weeklyTasks.groupBy { dateUtils.fromIsoDateString(it.date) }
            _uiState.update { currentState ->
                currentState.copy(
                    tasksByDate = tasksGroupedByDate,
                    weekDays = dateUtils.getWeekDays(currentWeekMonday.value), // ensure weekDays is current
                    isLoading = false,
                    currentWeekHeader = generateWeekHeader(currentWeekMonday.value)
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun generateWeekHeader(referenceDate: LocalDate): String {
        val weekDays = dateUtils.getWeekDays(referenceDate)
        val firstDay = weekDays.first()
        val lastDay = weekDays.last()
        return if (firstDay.month == lastDay.month) {
            "${dateUtils.getMonthDisplayName(firstDay)} ${firstDay.year}"
        } else {
            "${dateUtils.getMonthDisplayName(firstDay, style = java.time.format.TextStyle.SHORT)} - ${dateUtils.getMonthDisplayName(lastDay, style = java.time.format.TextStyle.SHORT)} ${lastDay.year}"
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        // If selected date changes the week, update currentWeekMonday
        val newWeekMonday = dateUtils.getWeekDays(date).firstOrNull() ?: date
        if (newWeekMonday != currentWeekMonday.value) {
            currentWeekMonday.value = newWeekMonday
        }
    }

    fun goToNextWeek() {
        currentWeekMonday.value = dateUtils.getNextWeek(currentWeekMonday.value)
        // Select the Monday of the new week by default
        _uiState.update { it.copy(selectedDate = currentWeekMonday.value) }
    }

    fun goToPreviousWeek() {
        currentWeekMonday.value = dateUtils.getPreviousWeek(currentWeekMonday.value)
        // Select the Monday of the new week by default
        _uiState.update { it.copy(selectedDate = currentWeekMonday.value) }
    }

    fun goToToday() {
        val today = dateUtils.getCurrentDate()
        currentWeekMonday.value = dateUtils.getWeekDays(today).firstOrNull() ?: today
        _uiState.update { it.copy(selectedDate = today) }
    }

    fun addTask(title: String, date: LocalDate) {
        if (title.isBlank()) return // Basic validation

        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                date = dateUtils.toIsoDateString(date),
                // orderIndex will be handled by Repository
            )
            taskRepository.insertTask(task)
            // Data will refresh due to Flow observation
        }
    }

    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompletion(taskId)
            // Data will refresh due to Flow observation
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            // Data will refresh due to Flow observation
        }
    }
}

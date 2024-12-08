package com.djordjekrutil.tcp.feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djordjekrutil.tcp.core.extension.toLocalDate
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow<TasksScreenState>(TasksScreenState.Loading)
    val screenState: StateFlow<TasksScreenState> = _screenState.asStateFlow()

    private var selectedDate: LocalDate = LocalDate.now()

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        getTasksUseCase(UseCase.None()) { result ->
            when (result) {
                is Either.Right -> {
                    viewModelScope.launch {
                        val tasks = result.b
                            .map { tasks ->
                                tasks.filter { it.targetDate.toLocalDate() == selectedDate }
                                    .sortedByDescending { it.priority }
                            }
                        _screenState.value =
                            TasksScreenState.Content(tasks, selectedDate)
                    }
                }

                is Either.Left -> {
                    _screenState.value =
                        TasksScreenState.Error("Failed to fetch tasks: ${result.a}")
                }
            }
        }
    }

    fun changeDate(forward: Boolean) {
        selectedDate = if (forward) selectedDate.plusDays(1) else selectedDate.minusDays(1)
        fetchTasks()
    }

    fun isSelectedDateToday(): Boolean {
        return selectedDate == LocalDate.now()
    }
}

sealed class TasksScreenState {
    data object Loading : TasksScreenState()
    data class Content(val tasks: Flow<List<TaskEntity>>, val date: LocalDate) : TasksScreenState()
    data class Error(val message: String) : TasksScreenState()
    data object Refreshing : TasksScreenState()
}
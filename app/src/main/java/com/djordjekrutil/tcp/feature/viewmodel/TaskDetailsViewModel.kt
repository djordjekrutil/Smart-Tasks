package com.djordjekrutil.tcp.feature.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.feature.model.TaskEntity
import com.djordjekrutil.tcp.feature.usecase.AddCommentUseCase
import com.djordjekrutil.tcp.feature.usecase.GetTaskUseCase
import com.djordjekrutil.tcp.feature.usecase.ResolveTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTaskUseCase: GetTaskUseCase,
    private val resolveTaskUseCase: ResolveTaskUseCase,
    private val addCommentUseCase: AddCommentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TaskDetailsState>(TaskDetailsState.Loading)
    val state: StateFlow<TaskDetailsState> = _state.asStateFlow()

    private val taskId: String = savedStateHandle["id"] ?: ""

    init {
        getTaskDetails(taskId)
    }

    private fun getTaskDetails(taskId: String) {
        getTaskUseCase(taskId) { result ->
            when (result) {
                is Either.Right -> {
                    _state.value = TaskDetailsState.Content(result.b)
                }

                is Either.Left -> {
                    _state.value = TaskDetailsState.Error("Failed to fetch task!!!")
                }
            }
        }
    }

    fun resolveTask(isResolved: Boolean) {
        resolveTaskUseCase(ResolveTaskUseCase.Params(taskId, isResolved)) { result ->
            when (result) {
                is Either.Right -> {}
                is Either.Left -> {
                    _state.value = TaskDetailsState.Error("Failed to resolve task!!!")
                }
            }
        }
    }

    fun addComment(comment: String) {
        addCommentUseCase(AddCommentUseCase.Params(taskId, comment)) { result ->
            when (result) {
                is Either.Right -> {}
                is Either.Left -> {
                    _state.value = TaskDetailsState.Error("Failed to add comment!!!")
                }
            }
        }
    }
}

sealed class TaskDetailsState {
    data object Loading : TaskDetailsState()
    data class Error(val message: String) : TaskDetailsState()
    data class Content(val task: Flow<TaskEntity>) : TaskDetailsState()
}
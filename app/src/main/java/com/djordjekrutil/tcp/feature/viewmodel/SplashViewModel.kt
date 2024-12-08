package com.djordjekrutil.tcp.feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.feature.usecase.SyncTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val syncTasksUseCase: SyncTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        syncTasks()
    }

    private fun syncTasks() {
        viewModelScope.launch {
            delay(1400)
            var progressBarJob: Job? = null

            try {
                progressBarJob = launch {
                    delay(2000)
                    _state.value = SplashState.ShowProgress
                }

                syncTasksUseCase(UseCase.None()) { result ->
                    progressBarJob.cancel()
                    when (result) {
                        is Either.Right -> {
                            _state.value = SplashState.NavigateToNextScreen
                        }

                        is Either.Left -> {
                            _state.value = SplashState.Error("Failed to sync tasks: ${result.a}")
                        }
                    }
                }
            } catch (e: Exception) {
                progressBarJob?.cancel()
                _state.value = SplashState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }
}

sealed class SplashState {
    data object Loading : SplashState()
    data object ShowProgress : SplashState()
    data object NavigateToNextScreen : SplashState()
    data class Error(val message: String) : SplashState()
}

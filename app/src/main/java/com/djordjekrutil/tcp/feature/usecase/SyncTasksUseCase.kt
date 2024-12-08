package com.djordjekrutil.tcp.feature.usecase

import com.djordjekrutil.tcp.core.exception.Failure
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.feature.repository.TasksRepository
import javax.inject.Inject

class SyncTasksUseCase @Inject constructor(
    private val tasksRepository: TasksRepository
) : UseCase<UseCase.None, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, None> {
        return tasksRepository.syncTasks()
    }
}
package com.djordjekrutil.tcp.feature.usecase

import com.djordjekrutil.tcp.core.exception.Failure
import com.djordjekrutil.tcp.core.functional.Either
import com.djordjekrutil.tcp.core.interactor.UseCase
import com.djordjekrutil.tcp.feature.repository.TasksRepository
import javax.inject.Inject

class ResolveTaskUseCase @Inject constructor(
    private val tasksRepository: TasksRepository
) : UseCase<UseCase.None, ResolveTaskUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, None> {
        return tasksRepository.resolveTask(params.taskId, params.isResolved)
    }

    data class Params(
        val taskId: String,
        val isResolved: Boolean
    )
}
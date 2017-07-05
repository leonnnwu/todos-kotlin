package todo.android.lwu.com.todos.tasks.domain.usecase

import todo.android.lwu.com.todos.UseCase
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 7/4/17.
 */
class GetTasks(
        val tasksRepository: TasksRepository
): UseCase<UseCase.RequestValues, UseCase.ResponseValues>() {

    override fun executeUseCase(requestValue: RequestValues) {
        if (requestValue.)
    }


    class RequestValues: UseCase.RequestValues {

    }
}
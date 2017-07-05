package todo.android.lwu.com.todos

import android.content.Context
import todo.android.lwu.com.todos.data.FakeTasksRemoteDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.data.source.local.TasksLocalDataSource

/**
 * Created by lwu on 5/14/17.
 *
 * Enable injection of mock implementations for {@link TasksDataSource} at compile time. This is useful
 * for testing, since it allows us to use a fake instance of the class to isolate the dependencies and run a test hermetically
 */
object Injection {

    fun provideTasksRepository(context: Context): TasksRepository {
        return TasksRepository.getInstance(FakeTasksRemoteDataSource, TasksLocalDataSource.getInstance(context))
    }

    fun provideGetTasks(context: Context): GetTasks {

    }
}
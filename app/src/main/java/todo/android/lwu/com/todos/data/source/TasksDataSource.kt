package todo.android.lwu.com.todos.data.source

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/23/17.
 */
interface TasksDataSource {

    fun getAllTasks(onTasksLoaded: (List<Task>) -> Unit)

    fun getTask(taskId: String, onTaskLoaded: (Task) -> Unit)

    fun saveTask(task: Task)

    fun clearCompletedTasks()

    fun deleteAllTasks()

    fun deleteTask(taskId: String)

    fun completeTask(task: Task)

    fun completeTask(taskId: String)

    fun activateTask(task: Task)

    fun activateTask(taskId: String)

    fun refreshTasks()
}
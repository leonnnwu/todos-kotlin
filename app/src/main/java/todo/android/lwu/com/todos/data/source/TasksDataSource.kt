package todo.android.lwu.com.todos.data.source

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/23/17.
 */
interface TasksDataSource {

    fun getAllTasks()

    fun getTask(taskId: String)

    fun saveTask(task: Task)

    fun clearCompletedTasks()

    fun deleteAllTasks()

    fun deleteTask(taskId: String)
}
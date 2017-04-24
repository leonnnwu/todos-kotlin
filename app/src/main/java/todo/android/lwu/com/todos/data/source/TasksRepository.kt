package todo.android.lwu.com.todos.data.source

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/23/17.
 */
object TasksRepository: TasksDataSource {

    private lateinit var tasksRemoteDataSource: TasksDataSource

    fun init(tasksRemoteDataSource: TasksDataSource) {
        this.tasksRemoteDataSource = tasksRemoteDataSource
    }

    override fun getAllTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearCompletedTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
package todo.android.lwu.com.todos.tasks

import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 4/3/17.
 */
class TasksPresenter(private val tasksRepository: TasksRepository, private val tasksView: TasksContract.View) : TasksContract.Presenter {

    override fun loadTasks(forceUpdate: Boolean) {
    }

    override fun openTaskDetails(requestedTask: Task) {
    }

    override fun completeTask(completedTask: Task) {
    }

    override fun activateTask(activeTask: Task) {
    }

    override fun start() {
    }

    init {
        tasksView.setPresenter(this)
    }
}
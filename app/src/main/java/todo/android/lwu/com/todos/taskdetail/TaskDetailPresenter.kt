package todo.android.lwu.com.todos.taskdetail

import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 6/25/17.
 */
class TaskDetailPresenter(
        val taskId: String,
        val taskDetailView: TaskDetailContract.View,
        val tasksRepository: TasksRepository
): TaskDetailContract.Presenter {

    init {
        taskDetailView.setPresenter(this)
    }

    override fun start() {
        openTask()
    }

    override fun editTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask() {
        tasksRepository.completeTask(taskId)
        taskDetailView.showTaskMarkedComplete()
    }

    override fun deleteTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask() {
        tasksRepository.activateTask(taskId)
        taskDetailView.showTaskMarkedActive()
    }

    private fun openTask() {
        taskDetailView.setLoadingIndicator(true)

        tasksRepository.getTask(taskId, object: TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task?) {
                if (!taskDetailView.isActive()) {
                    return
                }

                taskDetailView.setLoadingIndicator(false)

                if (task == null) {
                    taskDetailView.showMissingTask()
                } else {
                    showTask(task)
                }
            }

            override fun onDataNotAvailable() {
                if (!taskDetailView.isActive()) {
                    return
                }

                taskDetailView.showMissingTask()
            }
        })
    }

    private fun showTask(task: Task) {
        taskDetailView.showCompletionStatus(task.completed)

        if (task.title.isEmpty()) {
            taskDetailView.hideTitle()
        } else {
            taskDetailView.showTitle(task.title)
        }

        if (task.description.isEmpty()) {
            taskDetailView.hideDescription()
        } else {
            taskDetailView.showDescription(task.description)
        }

    }

}
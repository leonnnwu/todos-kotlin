package todo.android.lwu.com.todos.taskdetail

import android.content.Intent
import rx.subscriptions.CompositeSubscription
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.utils.schedulers.BaseSchedulerProvider

/**
 * Created by lwu on 6/25/17.
 */
class TaskDetailPresenter(
        val taskId: String,
        val taskDetailView: TaskDetailContract.View,
        val tasksRepository: TasksRepository,
        private val schedulerProvider: BaseSchedulerProvider
): TaskDetailContract.Presenter {

    private val subscription = CompositeSubscription()

    init {
        taskDetailView.setPresenter(this)
    }

    override fun subscribe() {
        openTask()
    }

    override fun unsubscribe() {
        subscription.clear()
    }

    override fun editTask() {
        if (taskId.isNullOrEmpty()) {
            taskDetailView.showMissingTask()
            return
        }

        taskDetailView.showEditTask(taskId)
    }

    override fun completeTask() {
        if (taskId.isNullOrEmpty()) {
            taskDetailView.showMissingTask()
            return
        }

        tasksRepository.completeTask(taskId)
        taskDetailView.showTaskMarkedComplete()
    }

    override fun deleteTask() {
        if (taskId.isNullOrEmpty()) {
            taskDetailView.showMissingTask()
            return
        }

        tasksRepository.deleteTask(taskId)
        taskDetailView.showTaskDeleted()
    }

    override fun activateTask() {
        if (taskId.isNullOrEmpty()) {
            taskDetailView.showMissingTask()
            return
        }

        tasksRepository.activateTask(taskId)
        taskDetailView.showTaskMarkedActive()
    }

    private fun openTask() {
        if (taskId.isNullOrEmpty()) {
            taskDetailView.showMissingTask()
        }

        taskDetailView.setLoadingIndicator(true)

        subscription.add(
                tasksRepository
                        .getTask(taskId)
                        .subscribeOn(schedulerProvider.computation())
                        .observeOn(schedulerProvider.ui())
                        .subscribe(
                                { onNext ->
                                    if (taskDetailView.isActive()) {
                                        showTask(onNext)
                                    }
                                },
                                { onError ->
                                    if (taskDetailView.isActive()) {
                                        taskDetailView.showMissingTask()
                                    }
                                },
                                {
                                    taskDetailView.setLoadingIndicator(false)
                                }
                        )
        )
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
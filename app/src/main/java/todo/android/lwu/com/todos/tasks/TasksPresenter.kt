package todo.android.lwu.com.todos.tasks

import android.app.Activity
import timber.log.Timber
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 4/3/17.
 */
class TasksPresenter(private val tasksRepository: TasksRepository, private val tasksView: TasksContract.View) : TasksContract.Presenter {
    private var currentFiltering = TasksFilterType.ALL_TASKS
    private var firstLoad = true

    init {
        tasksView.setPresenter(this)
    }

    override fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            tasksView.showSuccessfullySavedMessage()
        }
    }

    override fun openTaskDetails(requestedTask: Task) {
    }

    override fun completeTask(completedTask: Task) {
        tasksRepository.completeTask(completedTask)
        tasksView.showTaskMarkedComplete()
        loadTasks(false, false)
    }

    override fun activateTask(activeTask: Task) {
        tasksRepository.activateTask(activeTask)
        tasksView.showTaskMarkedActivate()
        loadTasks(false, false)
    }

    override fun addNewTask() {
        tasksView.showAddTask()
    }

    override fun clearCompletedTasks() {
        Timber.d("Clear completed tasks!")
    }

    override fun setFiltering(requestType: TasksFilterType) {
        currentFiltering =  requestType
    }

    override fun getFiltering(): TasksFilterType {
        return currentFiltering
    }

    override fun start() {
        loadTasks(false)
    }

    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {

        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true)
        }

        if (forceUpdate) {
            tasksRepository.refreshTasks()
        }

        tasksRepository.getAllTasks(object: TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = tasks.filter {
                    when (currentFiltering) {
                        TasksFilterType.ALL_TASKS -> true
                        TasksFilterType.ACTIVE_TASKS -> it.isActive()
                        TasksFilterType.COMPLETED_TASKS -> it.completed
                        else -> true
                    }
                }

                if (!tasksView.isActive()) {
                    return
                }

                if (showLoadingUI) {
                    tasksView.setLoadingIndicator(false)
                }


                if (tasksToShow.isEmpty()) {
                    when (currentFiltering) {
                        TasksFilterType.ACTIVE_TASKS -> tasksView.showNoActiveTasks()
                        TasksFilterType.COMPLETED_TASKS -> tasksView.showNoCompletedTasks()
                        else -> tasksView.showNoTasks()
                    }
                } else {
                    tasksView.showTasks(tasksToShow)

                    when (currentFiltering) {
                        TasksFilterType.ACTIVE_TASKS -> tasksView.showActiveFilterLabel()
                        TasksFilterType.COMPLETED_TASKS -> tasksView.showCompletedFilterLabel()
                        else -> tasksView.showAllFilterLabel()
                    }
                }
            }

            override fun onDataNotAvailable() {
                if (tasksView.isActive()) {
                    tasksView.showLoadingTasksError()
                }
            }
        })
    }
}
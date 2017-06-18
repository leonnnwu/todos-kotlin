package todo.android.lwu.com.todos.tasks

import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.events.TasksDownloadedEvent

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

    override fun openTaskDetails(requestedTask: Task) {
    }

    override fun completeTask(completedTask: Task) {
    }

    override fun activateTask(activeTask: Task) {
    }

    override fun addNewTask() {
        Timber.d("Show Add New Task View!")
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

        tasksRepository.getAllTasks { taskList ->
            val tasksToShow = taskList.filter {
                when (currentFiltering) {
                    TasksFilterType.ALL_TASKS -> true
                    TasksFilterType.ACTIVE_TASKS -> it.isActive()
                    TasksFilterType.COMPLETED_TASKS -> it.completed
                    else -> true
                }
            }

            if (!tasksView.isActive()) {
                return@getAllTasks
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
    }
}
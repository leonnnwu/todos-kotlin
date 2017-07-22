package todo.android.lwu.com.todos.tasks

import android.app.Activity
import rx.Observable
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.utils.schedulers.BaseSchedulerProvider

/**
 * Created by lwu on 4/3/17.
 */
class TasksPresenter(private val tasksRepository: TasksRepository,
                     private val tasksView: TasksContract.View,
                     private val schedulerProvider: BaseSchedulerProvider
) : TasksContract.Presenter {
    private var currentFiltering = TasksFilterType.ALL_TASKS
    private var firstLoad = true
    private val subscriptions = CompositeSubscription()

    init {
        tasksView.setPresenter(this)
    }

    override fun subscribe() {
        loadTasks(false)
    }

    override fun unsubscribe() {
        subscriptions.clear()
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
        tasksView.showTaskDetail(requestedTask.id)
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
        tasksRepository.clearCompletedTasks()
        tasksView.showCompletedTaskClear()
        loadTasks(false, false)
    }

    override fun setFiltering(requestType: TasksFilterType) {
        currentFiltering =  requestType
    }

    override fun getFiltering(): TasksFilterType {
        return currentFiltering
    }

    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true)
        }

        if (forceUpdate) {
            tasksRepository.refreshTasks()
        }

        subscriptions.clear()

        val subscription = tasksRepository
                .getAllTasks()
                .flatMap {
                    Observable.from(it)
                }
                .filter(this::filterTask)
                .toList()
                .subscribeOn(schedulerProvider.computation())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { onNext ->
                            Timber.d("onNext: ${onNext.count()}")
                            this.processTasks(onNext)
                        }
                        ,
                        { onError ->
                            Timber.d("onError")
                            tasksView.showLoadingTasksError()
                        },
                        {
                            Timber.d("onComplete")
                            tasksView.setLoadingIndicator(false)
                        }
                )

        subscriptions.add(subscription)
    }

    private fun processTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            processEmptyTask()
        } else {
            tasksView.showTasks(tasks)
            showFilterLabel()
        }
    }

    private fun processEmptyTask() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showNoActiveTasks()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showNoCompletedTasks()
            else -> tasksView.showNoTasks()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> tasksView.showActiveFilterLabel()
            TasksFilterType.COMPLETED_TASKS -> tasksView.showCompletedFilterLabel()
            else -> tasksView.showAllFilterLabel()
        }
    }

    private fun filterTask(task: Task): Boolean {
        return when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> task.isActive()
            TasksFilterType.COMPLETED_TASKS -> task.completed
            else -> true
        }
    }
}
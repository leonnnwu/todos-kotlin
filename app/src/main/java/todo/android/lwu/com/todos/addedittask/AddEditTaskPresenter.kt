package todo.android.lwu.com.todos.addedittask

import rx.subscriptions.CompositeSubscription
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.utils.schedulers.BaseSchedulerProvider

/**
 * Created by lwu on 6/24/17.
 */
class AddEditTaskPresenter(
    val taskId: String?,
    val tasksRepository: TasksRepository,
    val addTaskView: AddEditTaskContract.View,
    shouldLoadDataFromRepo: Boolean,
    private val schedulerProvider: BaseSchedulerProvider
): AddEditTaskContract.Presenter{

    private var isDataMissing: Boolean
    private val subscriptions = CompositeSubscription()

    init {
        addTaskView.setPresenter(this)
        isDataMissing = shouldLoadDataFromRepo
    }

    override fun subscribe() {
        if (!isNewTask() && isDataMissing) {
            populateTask()
        }
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun saveTask(title: String, description: String) {
        if (isNewTask()) {
            createNewTask(title, description)
        } else {
            updateTask(title, description)
        }
    }

    override fun populateTask() {
        if (taskId != null) {
            subscriptions.add(
                    tasksRepository.getTask(taskId)
                            .subscribeOn(schedulerProvider.computation())
                            .observeOn(schedulerProvider.ui())
                            .subscribe(
                                    { onNext ->
                                        if (addTaskView.isActive() && onNext != null) {
                                            addTaskView.setTitle(onNext.title)
                                            addTaskView.setDescription(onNext.description)
                                        }
                                    },
                                    { onError ->
                                        if (addTaskView.isActive()) {
                                            addTaskView.showEmptyTaskError()
                                        }
                                    }
                            )
            )
        }
    }

    override fun isDataMissing(): Boolean = isDataMissing

    private fun isNewTask(): Boolean = taskId == null

    private fun createNewTask(title: String, description: String) {
        val newTask = Task(title = title, description = description)

        if (newTask.isEmpty()) {
            addTaskView.showEmptyTaskError()
        } else {
            tasksRepository.saveTask(newTask)
            addTaskView.showTasksList()
        }
    }

    private fun updateTask(title: String, description: String) {
        if (taskId != null) {
            tasksRepository.saveTask(Task(title = title, description = description, id = taskId))
            addTaskView.showTasksList()
        }
    }
}
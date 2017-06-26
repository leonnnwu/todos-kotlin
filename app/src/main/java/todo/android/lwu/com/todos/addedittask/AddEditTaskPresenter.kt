package todo.android.lwu.com.todos.addedittask

import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 6/24/17.
 */
class AddEditTaskPresenter(
    val taskId: String?,
    val tasksRepository: TasksRepository,
    val addTaskView: AddEditTaskContract.View,
    shouldLoadDataFromRepo: Boolean
): AddEditTaskContract.Presenter{

    private var isDataMissing: Boolean

    init {
        addTaskView.setPresenter(this)
        isDataMissing = shouldLoadDataFromRepo
    }

    override fun start() {
        if (!isNewTask() && isDataMissing) {
            populateTask()
        }
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
            tasksRepository.getTask(taskId, object : TasksDataSource.GetTaskCallback {

                override fun onTaskLoaded(task: Task?) {
                    if (addTaskView.isActive() && task != null) {
                        addTaskView.setTitle(task.title)
                        addTaskView.setDescription(task.description)
                    }
                }

                override fun onDataNotAvailable() {
                    if (addTaskView.isActive()) {
                        addTaskView.showEmptyTaskError()
                    }
                }
            })
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
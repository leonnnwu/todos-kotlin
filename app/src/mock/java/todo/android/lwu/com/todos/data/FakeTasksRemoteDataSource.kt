package todo.android.lwu.com.todos.data

import org.greenrobot.eventbus.EventBus
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.remote.TasksRemoteDataSource
import todo.android.lwu.com.todos.events.TasksDownloadedEvent

/**
 * Created by lwu on 5/14/17.
 */
object FakeTasksRemoteDataSource: TasksDataSource {
    val TASKS_SERVICE_DATA = emptyMap<String, Task>().toMutableMap()

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASKS_SERVICE_DATA.put(newTask.id, newTask)
    }

    override fun getAllTasks(onTasksLoaded: (List<Task>) -> Unit) {
        val tasks = TasksRemoteDataSource.TASKS_SERVICE_DATA.values.toList()
        onTasksLoaded(tasks)
    }

    override fun getTask(taskId: String, onTaskLoaded: (Task) -> Unit) {
        val task = TasksRemoteDataSource.TASKS_SERVICE_DATA[taskId]

        if (task != null) {
            onTaskLoaded(task)
        }
    }

    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA.iterator().apply {
            this.forEach {
                if (it.component2().completed) {
                    this.remove()
                }
            }
        }
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    override fun completeTask(task: Task) {
        val completedTask = task.copy(completed = true)
        // Override task in data set
        TASKS_SERVICE_DATA.put(task.id, completedTask)
    }

    override fun completeTask(taskId: String) {
        TASKS_SERVICE_DATA[taskId]
                ?.run { this.copy(completed = true) }
                ?.let { TASKS_SERVICE_DATA.put(taskId, it) }
    }

    override fun activateTask(task: Task) {
        val activateTask = task.copy(completed = false)
        TASKS_SERVICE_DATA.put(task.id, activateTask)
    }

    override fun activateTask(taskId: String) {
        TASKS_SERVICE_DATA[taskId]
                ?.run { this.copy(completed = false) }
                ?.let { TASKS_SERVICE_DATA.put(taskId, it) }
    }
}
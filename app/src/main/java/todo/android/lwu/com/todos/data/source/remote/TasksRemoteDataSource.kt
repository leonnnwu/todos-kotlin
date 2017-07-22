package todo.android.lwu.com.todos.data.source.remote

import android.os.Handler
import rx.Observable
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import java.util.concurrent.TimeUnit

/**
 * Created by lwu on 4/23/17.
 */
object TasksRemoteDataSource: TasksDataSource {
    val TASKS_SERVICE_DATA = emptyMap<String, Task>().toMutableMap()
    val SERVICE_LATENCY_IN_MILLIS = 2000L

    init {
        addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
        addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title = title, description = description)
        TASKS_SERVICE_DATA.put(newTask.id, newTask)
    }

    override fun getAllTasks(): Observable<List<Task>> {
        return Observable
                .from(TASKS_SERVICE_DATA.values)
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList()
    }

    override fun getTask(taskId: String): Observable<Task> {

        val task = TASKS_SERVICE_DATA[taskId]

        if (task != null) {
            return Observable
                    .just(task)
                    .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
        } else {
            return Observable.empty()
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
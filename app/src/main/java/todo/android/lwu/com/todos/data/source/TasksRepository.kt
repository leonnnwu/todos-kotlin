package todo.android.lwu.com.todos.data.source

import org.greenrobot.eventbus.EventBus
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.events.TasksDownloadedEvent

/**
 * Created by lwu on 4/23/17.
 *
 * TODO: Add local tasks data source
 */
class TasksRepository private constructor(val tasksRemoteDataSource: TasksDataSource) : TasksDataSource {

    companion object {
        private val lockObject = Any()
        private var INSTANCE: TasksRepository? = null

        fun getInstance(tasksRemoteDataSource: TasksDataSource): TasksRepository {
            synchronized(lockObject) {
                return INSTANCE ?: TasksRepository(tasksRemoteDataSource)
            }
        }

        /**
         * Used to force {@link #getInstance(TasksDataSource)} to create a new instance next time it's called.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private val cachedTasks: MutableMap<String, Task>
    private var cacheIsDirty = false

    init {
        cachedTasks = mutableMapOf()
        tasksRemoteDataSource.getAllTasks {
            refreshCache(it)
        }
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun getTask(taskId: String, onTaskLoaded: (Task) -> Unit) {
        val cachedTask = getTaskWithId(taskId)

        if (cachedTask != null) {
            EventBus.getDefault().post(TasksDownloadedEvent.One(cachedTask))
        } else {
            tasksRemoteDataSource.getTask(taskId) { task ->
                cachedTasks.put(task.id, task)
                EventBus.getDefault().post(TasksDownloadedEvent.One(task))
            }
        }
    }

    override fun completeTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun completeTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activateTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllTasks(onTasksLoaded: (List<Task>) -> Unit) {

        when {
            !cacheIsDirty -> {
                val taskList = cachedTasks.values.toList()
                onTasksLoaded(taskList)
            }
            cacheIsDirty -> {
                tasksRemoteDataSource.getAllTasks {
                    refreshCache(it)
                    onTasksLoaded(it)
                }
            }
            else -> Unit //TODO: Get from local data source
        }
    }

    override fun saveTask(task: Task) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearCompletedTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAllTasks() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteTask(taskId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getTaskWithId(id: String): Task? {
        return cachedTasks[id]
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        tasks.associateBy {
            it.id
        }.let {
            cachedTasks.putAll(it)
        }
    }

}
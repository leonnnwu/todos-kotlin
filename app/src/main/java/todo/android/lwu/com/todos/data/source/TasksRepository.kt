package todo.android.lwu.com.todos.data.source

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/23/17.
 *
 */
class TasksRepository private constructor(val tasksRemoteDataSource: TasksDataSource,
                                          val tasksLocalDataSource: TasksDataSource) : TasksDataSource {

    companion object {
        private val lockObject = Any()
        private var INSTANCE: TasksRepository? = null

        fun getInstance(tasksRemoteDataSource: TasksDataSource,
                        tasksLocalDataSource: TasksDataSource): TasksRepository {
            synchronized(lockObject) {
                return INSTANCE ?: TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
            }
        }

        /**
         * Used to force {@link #getInstance(TasksDataSource)} to create a new instance next time it's called.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private var cachedTasks: MutableMap<String, Task> = mutableMapOf()
    private var cacheIsDirty = false

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun getAllTasks(callback: TasksDataSource.LoadTasksCallback) {

        when {
            cachedTasks.isNotEmpty() && !cacheIsDirty -> {
                val taskList = cachedTasks.values.toList()
                callback.onTasksLoaded(taskList)
            }
            cacheIsDirty -> {
                tasksRemoteDataSource.getAllTasks(object : TasksDataSource.LoadTasksCallback {
                    override fun onTasksLoaded(tasks: List<Task>) {
                        refreshCache(tasks)
                        refreshLocalDataSource(tasks)
                        callback.onTasksLoaded(tasks)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }

                })
            }
            else -> {
                tasksLocalDataSource.getAllTasks(object: TasksDataSource.LoadTasksCallback {
                    override fun onTasksLoaded(tasks: List<Task>) {
                        refreshCache(tasks)
                        callback.onTasksLoaded(cachedTasks.values.toList())
                    }

                    override fun onDataNotAvailable() {
                        getTasksFromRemoteDataSource(callback)
                    }
                })
            }
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val cachedTask = getTaskWithId(taskId)

        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask)
        } else {
            tasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
                override fun onTaskLoaded(task: Task?) {
                    if (task == null) {
                        onDataNotAvailable()
                    } else {
                        cachedTasks.put(task.id, task)
                        callback.onTaskLoaded(task)
                    }
                }

                override fun onDataNotAvailable() {
                    tasksRemoteDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
                        override fun onTaskLoaded(task: Task?) {
                            if (task == null) {
                                onDataNotAvailable()
                            } else {
                                cachedTasks.put(task.id, task)
                                callback.onTaskLoaded(task)
                            }
                        }

                        override fun onDataNotAvailable() {
                            callback.onDataNotAvailable()
                        }
                    })
                }
            })

        }
    }

    override fun completeTask(task: Task) {
        tasksRemoteDataSource.completeTask(task)
        tasksLocalDataSource.completeTask(task)

        cachedTasks.put(task.id, task.copy(completed = true))
    }

    override fun completeTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            completeTask(it)
        }
    }

    override fun activateTask(task: Task) {
        tasksRemoteDataSource.activateTask(task)
        tasksLocalDataSource.activateTask(task)

        cachedTasks.put(task.id, task.copy(completed = false))
    }

    override fun activateTask(taskId: String) {
        getTaskWithId(taskId)?.let {
            activateTask(it)
        }
    }

    override fun saveTask(task: Task) {
        tasksRemoteDataSource.saveTask(task)
        tasksLocalDataSource.saveTask(task)

        cachedTasks.put(task.id, task)
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        cachedTasks.iterator().run {
            while (this.hasNext()) {
                this.takeIf { it.next().value.completed }?.remove()
            }
        }
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()
        cachedTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)

        cachedTasks.remove(taskId)
    }

    private fun getTaskWithId(id: String): Task? {
        return cachedTasks[id]
    }

    private fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadTasksCallback) {
        tasksRemoteDataSource.getAllTasks(object: TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
                refreshLocalDataSource(tasks)
                callback.onTasksLoaded(cachedTasks.values.toList())
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        tasks.forEach {
            tasksLocalDataSource.saveTask(it)
        }
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
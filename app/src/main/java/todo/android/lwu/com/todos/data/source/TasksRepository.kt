package todo.android.lwu.com.todos.data.source

import rx.Observable
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

    var cachedTasks: MutableMap<String, Task> = mutableMapOf()
    private var cacheIsDirty = false

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun getAllTasks(): Observable<List<Task>> {
        if (cachedTasks.isNotEmpty() && !cacheIsDirty) {
            return Observable.from(cachedTasks.values).toList()
        }

        val remoteTasks = getAndSaveRemoteTasks()

        if (cacheIsDirty) {
            return remoteTasks
        } else {
            val localTasks = getAndCacheLocalTasks()

            return Observable.concat(localTasks, remoteTasks)
                    .filter {
                        it.isNotEmpty()
                    }.first()
        }
    }

    override fun getTask(taskId: String): Observable<Task> {
        val cachedTask = getTaskWithId(taskId)

        if (cachedTask != null) {
            return Observable.just(cachedTask)
        } else {
            val localTask = getTaskWithIdFromLocalRepository(taskId)
            val remoteTask = getTasksFromRemoteDataSource(taskId)
            return Observable
                    .concat(localTask, remoteTask)
                    .first()
                    .map { task ->
                        if (task == null) {
                            throw NoSuchElementException("No task found with taskId $taskId")
                        } else {
                            task
                        }
                    }
        }
    }

    private fun getTaskWithIdFromLocalRepository(taskId: String): Observable<Task> {
        return tasksLocalDataSource
                .getTask(taskId)
                .doOnNext {
                    cachedTasks.put(taskId, it)
                }.first()

    }

    private fun getTasksFromRemoteDataSource(taskId: String): Observable<Task> {
        return tasksRemoteDataSource
                .getTask(taskId)
                .doOnNext { task ->
                    tasksLocalDataSource.saveTask(task)
                    cachedTasks.put(taskId, task)
                }
    }

    private fun getAndCacheLocalTasks(): Observable<List<Task>> {
        return tasksLocalDataSource
                .getAllTasks()
                .flatMap { tasks ->
                    Observable.from(tasks).doOnNext { task ->
                        cachedTasks.put(task.id, task)
                    }
                }.toList()
    }

    private fun getAndSaveRemoteTasks(): Observable<List<Task>> {
        return tasksRemoteDataSource
                .getAllTasks()
                .flatMap { tasks ->
                    Observable.from(tasks).doOnNext { task ->
                        tasksLocalDataSource.saveTask(task)
                        cachedTasks.put(task.id, task)
                    }.toList()
                }
                .doOnCompleted { cacheIsDirty = false }
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

}
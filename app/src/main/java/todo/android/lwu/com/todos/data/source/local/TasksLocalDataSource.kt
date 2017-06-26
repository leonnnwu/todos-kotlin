package todo.android.lwu.com.todos.data.source.local

import android.content.ContentValues
import android.content.Context
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.local.TasksPersistenceContract.TaskEntry

/**
 * Created by lwu on 6/18/17.
 */
class TasksLocalDataSource private constructor(context: Context): TasksDataSource{

    companion object {
        private var instance: TasksLocalDataSource? = null

        fun getInstance(context: Context): TasksDataSource {
            synchronized(TasksLocalDataSource) {
                return instance ?: TasksLocalDataSource(context)
            }
        }
    }

    private val dbHelper: TasksDbHelper = TasksDbHelper(context)

    override fun getAllTasks(callback: TasksDataSource.LoadTasksCallback) {
        val tasks = arrayListOf<Task>()
        dbHelper.getCursor()?.takeIf { cursor ->
            cursor.count > 0
        }?.run {
            while (this.moveToNext()) {
                tasks.add(Task(
                        id = this.getString(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID)),
                        title = this.getString(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE)),
                        description = this.getString(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION)),
                        completed = this.getInt(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1
                ))
            }

            this.close()
        }

        dbHelper.close()

        if (tasks.isEmpty()) {
            callback.onDataNotAvailable()
        } else {
            callback.onTasksLoaded(tasks)
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {

        val task = dbHelper.getCursor(taskId)?.takeIf { cursor ->
            cursor.count > 0
        }?.run {
            this.moveToFirst()
            val task = Task(id = this.getString(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID)),
                    title = this.getString(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE)),
                    description = this.getString(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION)),
                    completed = this.getInt(this.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1
            )

            this.close()

            task
        }

        dbHelper.close()

        if (task != null) {
            callback.onTaskLoaded(task)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override fun saveTask(task: Task) {
        dbHelper.insert(task.contentValues())
        dbHelper.close()
    }

    override fun completeTask(task: Task) {
        completeTask(task.id)
    }

    override fun completeTask(taskId: String) {
        dbHelper.saveCompletedStatus(true, taskId)
        dbHelper.close()
    }

    override fun activateTask(task: Task) {
        activateTask(task.id)
    }

    override fun activateTask(taskId: String) {
        dbHelper.saveCompletedStatus(false, taskId)
        dbHelper.close()
    }

    override fun clearCompletedTasks() {
        dbHelper.writableDatabase.delete(TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_COMPLETED + "= ?", arrayOf("1"))
        dbHelper.close()
    }

    override fun deleteAllTasks() {
        dbHelper.writableDatabase.delete(TaskEntry.TABLE_NAME, null, null)
        dbHelper.close()
    }

    override fun deleteTask(taskId: String) {
        dbHelper.writableDatabase.delete(TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?", arrayOf(taskId))
        dbHelper.close()
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

}
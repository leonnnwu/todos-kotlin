package todo.android.lwu.com.todos.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.squareup.sqlbrite.BriteDatabase
import com.squareup.sqlbrite.SqlBrite
import rx.Observable
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.local.TasksPersistenceContract.TaskEntry
import todo.android.lwu.com.todos.utils.schedulers.BaseSchedulerProvider

/**
 * Created by lwu on 6/18/17.
 */
class TasksLocalDataSource private constructor(context: Context,
                                               schedulerProvider: BaseSchedulerProvider): TasksDataSource{

    companion object {
        private var instance: TasksLocalDataSource? = null

        fun getInstance(context: Context, schedulerProvider: BaseSchedulerProvider): TasksDataSource {
            synchronized(TasksLocalDataSource) {
                return instance ?: TasksLocalDataSource(context, schedulerProvider)
            }
        }
    }

    private val dbHelper: BriteDatabase = SqlBrite.create().wrapDatabaseHelper(TasksDbHelper(context), schedulerProvider.io())


    override fun getAllTasks(): Observable<List<Task>> {

        val projection = arrayOf(
            TaskEntry.COLUMN_NAME_ENTRY_ID,
            TaskEntry.COLUMN_NAME_TITLE,
            TaskEntry.COLUMN_NAME_DESCRIPTION,
            TaskEntry.COLUMN_NAME_COMPLETED
        )

        val sql = "SELECT ${projection.joinToString(",")} FROM ${TaskEntry.TABLE_NAME}"
        return dbHelper.createQuery(TaskEntry.TABLE_NAME, sql).mapToList(this::getTask)

    }

    override fun getTask(taskId: String): Observable<Task> {

        val projection = arrayOf(
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        )

        val sql = "SELECT ${projection.joinToString(",")} FROM ${TaskEntry.TABLE_NAME} WHERE ${TaskEntry.COLUMN_NAME_ENTRY_ID} IS $taskId"
        return dbHelper.createQuery(TaskEntry.TABLE_NAME, sql).mapToOneOrDefault(this::getTask, null)
    }

    override fun saveTask(task: Task) {
        dbHelper.insert(TaskEntry.TABLE_NAME, task.contentValues(), SQLiteDatabase.CONFLICT_REPLACE)
    }

    override fun completeTask(task: Task) {
        completeTask(task.id)
    }

    override fun completeTask(taskId: String) {
        dbHelper.update(TaskEntry.TABLE_NAME,
                ContentValues().apply { put(TaskEntry.COLUMN_NAME_COMPLETED, true) },
                "${TaskEntry.COLUMN_NAME_ENTRY_ID} IS ?",
                taskId)
    }

    override fun activateTask(task: Task) {
        activateTask(task.id)
    }

    override fun activateTask(taskId: String) {
        dbHelper.update(TaskEntry.TABLE_NAME,
                ContentValues().apply { put(TaskEntry.COLUMN_NAME_COMPLETED, false) },
                "${TaskEntry.COLUMN_NAME_ENTRY_ID} IS ?",
                taskId)
    }

    override fun clearCompletedTasks() {
        dbHelper.delete(TaskEntry.TABLE_NAME,
                "${TaskEntry.COLUMN_NAME_ENTRY_ID} IS ?",
                "1")
    }

    override fun deleteAllTasks() {
        dbHelper.delete(TaskEntry.TABLE_NAME, null)
    }

    override fun deleteTask(taskId: String) {
        dbHelper.delete(TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_ENTRY_ID + " = ?", taskId)
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    private fun getTask(cursor: Cursor): Task {
        return Task(id = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION)),
                completed = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1)
    }

}
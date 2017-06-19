package todo.android.lwu.com.todos.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 6/18/17.
 */
class TasksDbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Tasks.db"
        const val DATABASE_VERSION = 1

        const val TEXT_TYPE = " TEXT"
        const val BOOLEAN_TYPE = " INTEGER"
        const val COMMA_SEP = ","

        const val SQL_CREATE_ENTRIES = "CREATE TABLE " + TasksPersistenceContract.TaskEntry.TABLE_NAME + "(" +
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + " PRIMARY KEY," +
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED + BOOLEAN_TYPE +
                ")"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Not required as at version 1
    }

    fun getCursor(): Cursor? {
        return readableDatabase.query(
                TasksPersistenceContract.TaskEntry.TABLE_NAME,
                arrayOf(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                        TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                        TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                        TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED),
                null, null, null, null, null
        )
    }

    fun insert(contentValues: ContentValues): Long {
        return this.writableDatabase.insert(TasksPersistenceContract.TaskEntry.TABLE_NAME, null, contentValues)
    }

    fun saveCompletedStatus(completed: Boolean, id: String) {
        writableDatabase.update(
                TasksPersistenceContract.TaskEntry.TABLE_NAME,
                ContentValues().apply { put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED, completed) },
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + "= ?",
                arrayOf(id)
        )
    }
}

fun Task.contentValues(): ContentValues {
    val result = ContentValues()
    result.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID, this.id)
    result.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE, this.title)
    result.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION, this.description)
    result.put(TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED, this.completed)
    return result
}
package todo.android.lwu.com.todos.data.source.local

import android.provider.BaseColumns

/**
 * Created by lwu on 6/18/17.
 */
object TasksPersistenceContract {

    abstract class TaskEntry: BaseColumns {
        companion object {
            const val TABLE_NAME = "tasks"
            const val COLUMN_NAME_ENTRY_ID = "entryid"
            const val COLUMN_NAME_TITLE = "title"
            const val COLUMN_NAME_DESCRIPTION = "description"
            const val COLUMN_NAME_COMPLETED = "completed"
        }
    }
}
package todo.android.lwu.com.todos.data

import java.util.*

/**
 * Created by lwu on 4/23/17.
 */
data class Task(
        val id: String = UUID.randomUUID().toString(),
        val title: String,
        val description: String = UUID.randomUUID().toString(),
        val completed: Boolean = false
) {
    fun getTitleForList(): String {
        if (!title.isNullOrEmpty()) {
            return title
        } else {
            return description
        }
    }

    fun isActive(): Boolean {
        return !completed
    }

    fun isEmpty(): Boolean {
        return title.isNullOrEmpty() && description.isNullOrEmpty()
    }
}

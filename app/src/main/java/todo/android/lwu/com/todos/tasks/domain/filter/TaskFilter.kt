package todo.android.lwu.com.todos.tasks.domain.filter

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 7/4/17.
 */
interface TaskFilter {
    fun filter(tasks: List<Task>): List<Task>
}
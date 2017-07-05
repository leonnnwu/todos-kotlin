package todo.android.lwu.com.todos.tasks.domain.filter

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 7/4/17.
 *
 * Returns the active tasks from a list of {@link Task}s
 */
class ActiveTaskFilter: TaskFilter {
    override fun filter(tasks: List<Task>): List<Task> = tasks.filter { it.isActive() }
}
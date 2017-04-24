package todo.android.lwu.com.todos.events

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/23/17.
 */
class TasksDownloadedEvent {
    class All(val taskList: List<Task>)
    class One(val task: Task?)
}

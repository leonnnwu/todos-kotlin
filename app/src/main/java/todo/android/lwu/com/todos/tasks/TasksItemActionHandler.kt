package todo.android.lwu.com.todos.tasks

import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 7/23/17.
 *
 * Listens to user actions from the list item in ({@link TasksFragment}) and redirects
 * them to the Fragment's actions listener.
 */
class TasksItemActionHandler(private val listener: TasksContract.Presenter) {

    /**
     * Called by the Data Binding Library when the checkbox is toggled.
     */
    fun completeChanged(task: Task, isChecked: Boolean) {
        if (isChecked) {
            listener.completeTask(task)
        } else {
            listener.activateTask(task)
        }
    }

    /**
     * Called by the Data Binding Library when the row is clicked.
     */
    fun taskClicked(task: Task) {
        listener.openTaskDetails(task)
    }
}
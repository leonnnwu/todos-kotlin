package todo.android.lwu.com.todos.taskdetail

import todo.android.lwu.com.todos.BasePresenter
import todo.android.lwu.com.todos.BaseView
import todo.android.lwu.com.todos.tasks.TasksContract

/**
 * Created by lwu on 6/25/17.
 */
class TaskDetailContract {

    interface View: BaseView<Presenter> {
        fun setLoadingIndicator(active: Boolean)

        fun showMissingTask()

        fun hideTitle()

        fun showTitle(title: String)

        fun hideDescription()

        fun showDescription(description: String)

        fun showCompletionStatus(complete: Boolean)

        fun showEditTask(taskId: String)

        fun showTaskDeleted()

        fun showTaskMarkedComplete()

        fun showTaskMarkedActive()

        fun isActive(): Boolean
    }

    interface Presenter: BasePresenter {
        fun editTask()

        fun completeTask()

        fun deleteTask()

        fun activateTask()
    }
}
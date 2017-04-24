package todo.android.lwu.com.todos.tasks

import todo.android.lwu.com.todos.BasePresenter
import todo.android.lwu.com.todos.BaseView
import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/3/17.
 */
interface TasksContract {
    interface View: BaseView<Presenter> {

    }

    interface Presenter: BasePresenter {

        fun loadTasks(forceUpdate: Boolean)

        fun openTaskDetails(requestedTask: Task)

        fun completeTask(completedTask: Task)

        fun activateTask(activeTask: Task)
    }
}
package todo.android.lwu.com.todos.tasks

import todo.android.lwu.com.todos.BasePresenter
import todo.android.lwu.com.todos.BaseView
import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/3/17.
 */
interface TasksContract {
    interface View: BaseView<Presenter> {

        fun showTasks(tasks: List<Task>)

        fun showNoTasks()

        fun showAddTask()

        fun showFilteringPopUpMenu()

        fun showActiveFilterLabel()

        fun showCompletedFilterLabel()

        fun showAllFilterLabel()

        fun isActive(): Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showNoActiveTasks()

        fun showNoCompletedTasks()

        fun showLoadingTasksError()

        fun showSuccessfullySavedMessage()

        fun showTaskMarkedComplete()

        fun showTaskMarkedActivate()

        fun showCompletedTaskClear()

        fun showTaskDetail(taskId: String)
    }

    interface Presenter: BasePresenter {

        fun loadTasks(forceUpdate: Boolean)

        fun openTaskDetails(requestedTask: Task)

        fun completeTask(completedTask: Task)

        fun activateTask(activeTask: Task)

        fun addNewTask()

        fun clearCompletedTasks()

        fun setFiltering(requestType: TasksFilterType)

        fun getFiltering(): TasksFilterType

        fun result(requestCode: Int, resultCode: Int)
    }
}

enum class TasksFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_TASKS,

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS
}
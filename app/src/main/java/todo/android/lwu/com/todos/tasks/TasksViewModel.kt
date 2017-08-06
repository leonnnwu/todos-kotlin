package todo.android.lwu.com.todos.tasks

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import todo.android.lwu.com.todos.BR
import todo.android.lwu.com.todos.R

/**
 * Created by lwu on 7/22/17.
 */
class TasksViewModel(private val context: Context,
                     private val presenter: TasksContract.Presenter
): BaseObservable() {
    private var taskListSize = 0

    @Bindable
    fun getCurrentFilteringLabel(): String {
        return when (presenter.getFiltering()) {
            TasksFilterType.ALL_TASKS -> context.resources.getString(R.string.label_all)
            TasksFilterType.COMPLETED_TASKS -> context.resources.getString(R.string.label_all)
            TasksFilterType.ACTIVE_TASKS -> context.resources.getString(R.string.label_active)
        }
    }

    @Bindable
    fun getNoTasksLabel(): String {
        return when (presenter.getFiltering()) {
            TasksFilterType.ALL_TASKS -> context.resources.getString(R.string.no_tasks_all)
            TasksFilterType.COMPLETED_TASKS -> context.resources.getString(R.string.no_tasks_completed)
            TasksFilterType.ACTIVE_TASKS -> context.resources.getString(R.string.no_tasks_active)
        }
    }

    @Bindable
    fun getNoTaskIconRes(): Drawable {
        return when (presenter.getFiltering()) {
            TasksFilterType.ALL_TASKS -> ContextCompat.getDrawable(context, R.drawable.ic_assignment_turned_in_24dp)
            TasksFilterType.COMPLETED_TASKS -> ContextCompat.getDrawable(context, R.drawable.ic_check_circle_24dp)
            TasksFilterType.ACTIVE_TASKS -> ContextCompat.getDrawable(context, R.drawable.ic_verified_user_24dp)
        }
    }

    @Bindable
    fun getTasksAddViewVisible(): Boolean {
        return presenter.getFiltering() == TasksFilterType.ALL_TASKS
    }

    @Bindable
    fun isNotEmpty(): Boolean {
        return taskListSize > 0
    }

    fun setTaskListSize(taskListSize: Int) {
        this.taskListSize = taskListSize
        notifyPropertyChanged(BR.noTaskIconRes)
        notifyPropertyChanged(BR.noTasksLabel)
        notifyPropertyChanged(BR.currentFilteringLabel)
        notifyPropertyChanged(BR.notEmpty)
        notifyPropertyChanged(BR.tasksAddViewVisible)
    }
}
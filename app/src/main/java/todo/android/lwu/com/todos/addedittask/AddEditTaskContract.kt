package todo.android.lwu.com.todos.addedittask

import todo.android.lwu.com.todos.BasePresenter
import todo.android.lwu.com.todos.BaseView

/**
 * Created by lwu on 6/24/17.
 */
class AddEditTaskContract {

    interface View: BaseView<Presenter> {

        fun showEmptyTaskError()

        fun showTasksList()

        fun setTitle(title: String)

        fun setDescription(description: String)

        fun isActive(): Boolean
    }

    interface Presenter: BasePresenter {

        fun saveTask(title: String, description: String)

        fun populateTask()

        fun isDataMissing(): Boolean
    }
}
package todo.android.lwu.com.todos.tasks

import todo.android.lwu.com.todos.BasePresenter
import todo.android.lwu.com.todos.BaseView

/**
 * Created by lwu on 4/3/17.
 */
interface TasksContract {
    interface View: BaseView<Presenter> {

    }

    interface Presenter: BasePresenter {

    }
}
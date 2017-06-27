package todo.android.lwu.com.todos.statistics

import todo.android.lwu.com.todos.BasePresenter
import todo.android.lwu.com.todos.BaseView

/**
 * Created by lwu on 6/26/17.
 */
class StatisticsContract {

    interface View: BaseView<StatisticsPresenter> {

        fun setProgressIndicator(active: Boolean)

        fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int)

        fun setLoadingStatisticsError()

        fun isActive(): Boolean
    }

    interface Presenter: BasePresenter {

    }
}
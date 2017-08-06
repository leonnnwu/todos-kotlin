package todo.android.lwu.com.todos.tasks

import android.databinding.BindingAdapter

/**
 * Created by lwu on 8/5/17.
 */
object SwipeRefreshLayoutDataBinding {

    @JvmStatic
    @BindingAdapter("android:onRefresh")
    fun setSwipeRefreshLayoutOnRefreshListener(view: ScrollChildSwipeRefreshLayout, presenter: TasksContract.Presenter) {
        view.setOnRefreshListener { presenter.loadTasks(true) }
    }

}
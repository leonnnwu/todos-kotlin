package todo.android.lwu.com.todos

/**
 * Created by lwu on 4/3/17.
 */
interface BaseView<T> {
    fun setPresenter(presenter: T)
}
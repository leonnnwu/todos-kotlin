package todo.android.lwu.com.todos.tasks

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

/**
 * Created by lwu on 4/23/17.
 */
class ScrollChildSwipeRefreshLayout(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {
    private var scrollUpChild: View? = null

    fun setScrollUpChild(view: View) {scrollUpChild = view}

    override fun canChildScrollUp(): Boolean {

        if (scrollUpChild != null) {
            return ViewCompat.canScrollVertically(scrollUpChild, -1)
        }

        return super.canChildScrollUp()
    }
}
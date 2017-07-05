package todo.android.lwu.com.todos.utils

import android.support.test.espresso.IdlingResource

/**
 * Created by lwu on 7/4/17.
 */
class EspressoIdlingResource {

    companion object {
        const val RESOURCE = "GLOBAL"
        private val DEFAULT_INSTANCE = SimpleCountingIdlingResource(RESOURCE)

        fun increment() { DEFAULT_INSTANCE.increment() }

        fun decrement() { DEFAULT_INSTANCE.decrement() }

        fun getIdlingResource(): IdlingResource { return DEFAULT_INSTANCE }
    }
}
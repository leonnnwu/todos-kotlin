package todo.android.lwu.com.todos.utils

import android.support.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by lwu on 7/4/17.
 *
 * A simple counter implementation of {@link IdlingResource} that determins idleness by
 * maintaining an internal counter. When the counter is 0 - it is considered to be idle, when it is non-zero
 * it is not idle. This is very similar to the way a {@link Semaphore} behaves.
 *
 * This class can then be used to wrap up operations that while in progress should block tests from accessing the UI.
 */
class SimpleCountingIdlingResource(val resourceName: String): IdlingResource {

    private val counter = AtomicInteger(0)

    // written from main thread, read from any thread.
    @Volatile private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return resourceName
    }

    override fun isIdleNow(): Boolean {
        return counter.get() == 0
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    fun increment() {
        counter.getAndIncrement()
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.

     * If this operation results in the counter falling below 0 - an exception is raised.

     * @throws IllegalStateException if the counter is below 0.
     */
    fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            if (null != resourceCallback) {
                resourceCallback!!.onTransitionToIdle()
            }
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}
package todo.android.lwu.com.todos.utils.schedulers

import rx.Scheduler

/**
 * Created by lwu on 7/4/17.
 *
 * Allow providing different types of {@link Scheduler}s
 */
interface BaseSchedulerProvider {

    fun computation(): Scheduler

    fun io(): Scheduler

    fun ui(): Scheduler
}
package todo.android.lwu.com.todos.utils.schedulers

import rx.Scheduler
import rx.schedulers.Schedulers

/**
 * Created by lwu on 7/4/17.
 *
 * Implementation of the {@link BaseSchedulerProvider} making all {@link Scheduler}s immediate
 */
class ImmediateSchedulerProvider: BaseSchedulerProvider {

    override fun computation(): Scheduler {
        return Schedulers.immediate()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return Schedulers.immediate()
    }
}
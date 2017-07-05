package todo.android.lwu.com.todos

import todo.android.lwu.com.todos.utils.EspressoIdlingResource

/**
 * Created by lwu on 7/4/17.
 *
 * Runs {@link UseCase}s using a {@link UseCaseScheduler}
 */
object UseCaseHandler {

    private val useCaseScheduler = UseCaseThreadPoolScheduler()

    fun <T: UseCase.RequestValues, R: UseCase.ResponseValues> execute(useCase: UseCase<T, R>, values: T, callback: UseCase.UseCaseCallback<R>) {
        useCase.requestValues = values
        useCase.useCaseCallback = UiCallbackWrapper(callback, this)

        // The network request might be handled in a different thread so make sure
        // Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment()

        useCaseScheduler.execute(Runnable {
            useCase.run()

            // This callback may be called twice, once for the cache and once for loading
            // the data from the server API, so we check before decrementing, otherwise
            // it throws "Counter has been corrupted!" exception.
            if (!EspressoIdlingResource.getIdlingResource().isIdleNow) {
                EspressoIdlingResource.decrement()
            }
        })
    }

    fun <V: UseCase.ResponseValues> notifyResponse(response: V, useCaseCallback: UseCase.UseCaseCallback<V>) {
        useCaseScheduler.notifyResponse(response, useCaseCallback)
    }

    fun <V: UseCase.ResponseValues> notifyError(useCaseCallback: UseCase.UseCaseCallback<V>) {
        useCaseScheduler.onError(useCaseCallback)
    }

    class UiCallbackWrapper<V: UseCase.ResponseValues>(val callback: UseCase.UseCaseCallback<V>,
                                                       val useCaseHandler: UseCaseHandler): UseCase.UseCaseCallback<V> {
        override fun onSuccess(response: V) {
            useCaseHandler.notifyResponse(response, callback)
        }

        override fun onError() {
            useCaseHandler.notifyError(callback)
        }

    }
}
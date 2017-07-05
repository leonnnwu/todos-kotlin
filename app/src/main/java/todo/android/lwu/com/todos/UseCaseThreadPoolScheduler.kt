package todo.android.lwu.com.todos

import android.os.Handler
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by lwu on 7/4/17.
 *
 * Executes asynchronous tasks using a {@link ThreadPoolExecutor}.
 * <p>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 */
class UseCaseThreadPoolScheduler: UseCaseScheduler {

    companion object {
        const val POOL_SIZE = 2
        const val MAX_POOL_SIZE = 4
        const val TIMEOUT = 30L
    }

    private val threadPoolExecutor: ThreadPoolExecutor

    init {
        threadPoolExecutor = ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(POOL_SIZE))
    }

    private val handler = Handler()

    override fun execute(runnable: Runnable) {
        threadPoolExecutor.execute(runnable)
    }

    override fun <V : UseCase.ResponseValues> notifyResponse(response: V, useCaseCallback: UseCase.UseCaseCallback<V>) {
        handler.post { useCaseCallback.onSuccess(response) }
    }

    override fun <V : UseCase.ResponseValues> onError(useCaseCallback: UseCase.UseCaseCallback<V>) {
        handler.post { useCaseCallback.onError() }
    }
}
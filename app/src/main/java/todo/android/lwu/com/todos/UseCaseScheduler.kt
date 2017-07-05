package todo.android.lwu.com.todos

/**
 * Created by lwu on 7/4/17.
 */
interface UseCaseScheduler {

    fun execute(runnable: Runnable)

    fun <V: UseCase.ResponseValues> notifyResponse(response: V, useCaseCallback: UseCase.UseCaseCallback<V>)

    fun <V: UseCase.ResponseValues> onError(useCaseCallback: UseCase.UseCaseCallback<V>)
}
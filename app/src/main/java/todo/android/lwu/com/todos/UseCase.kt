package todo.android.lwu.com.todos

/**
 * Created by lwu on 7/4/17.
 *
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
abstract class UseCase<Q: UseCase.RequestValues, P: UseCase.ResponseValues>{

    var requestValues: Q? = null

    var useCaseCallback: UseCaseCallback<P>? = null

    fun run() {
        requestValues?.let {
            executeUseCase(it)
        }
    }

    protected abstract fun executeUseCase(requestValue: Q)

    interface RequestValues

    interface ResponseValues

    interface UseCaseCallback<in R> {
        fun onSuccess(response: R)
        fun onError()
    }
}
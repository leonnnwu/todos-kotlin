package todo.android.lwu.com.todos.statistics

import rx.Observable
import rx.subscriptions.CompositeSubscription
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.utils.schedulers.SchedulerProvider
import java.util.function.BiFunction

/**
 * Created by lwu on 6/26/17.
 */
class StatisticsPresenter(
        val tasksRepository: TasksRepository,
        val statisticsView: StatisticsContract.View,
        private val schedulerProvider: SchedulerProvider
): StatisticsContract.Presenter {

    private val subscriptions = CompositeSubscription()

    init {
        statisticsView.setPresenter(this)
    }

    override fun subscribe() {
        loadStatistics()
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    private fun loadStatistics() {
        statisticsView.setProgressIndicator(true)

        subscriptions.add(
                tasksRepository.getAllTasks()
                        .map { tasks ->
                            Pair(tasks.count { it.completed }, tasks.count { it.isActive() })
                        }
                        .subscribeOn(schedulerProvider.computation())
                        .observeOn(schedulerProvider.ui())
                        .subscribe(
                                { onNext ->
                                    if (statisticsView.isActive()) {
                                        statisticsView.showStatistics(onNext.second, onNext.first)
                                    }
                                },
                                { onError ->
                                    if (statisticsView.isActive()) {
                                        statisticsView.setLoadingStatisticsError()
                                    }
                                },
                                {
                                    statisticsView.setProgressIndicator(false)
                                }
                        )
        )
    }
}
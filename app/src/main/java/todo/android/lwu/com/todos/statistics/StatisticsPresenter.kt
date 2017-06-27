package todo.android.lwu.com.todos.statistics

import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 6/26/17.
 */
class StatisticsPresenter(
        val tasksRepository: TasksRepository,
        val statisticsView: StatisticsContract.View
): StatisticsContract.Presenter {

    init {
        statisticsView.setPresenter(this)
    }


    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        statisticsView.setProgressIndicator(true)

        tasksRepository.getAllTasks(object: TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                var activeTasks = 0
                var completedTask = 0

                tasks.partition { it.completed }.run {
                    completedTask = first.count()
                    activeTasks = second.count()
                }

                if (!statisticsView.isActive()) {
                    return
                }

                statisticsView.setProgressIndicator(false)

                statisticsView.showStatistics(activeTasks, completedTask)
            }

            override fun onDataNotAvailable() {
                if (!statisticsView.isActive()) {
                    return
                }

                statisticsView.setLoadingStatisticsError()
            }
        })
    }
}
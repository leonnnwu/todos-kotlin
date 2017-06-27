package todo.android.lwu.com.todos.statistics

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.statistics_frag.*
import todo.android.lwu.com.todos.R

/**
 * Created by lwu on 6/26/17.
 */
class StatisticsFragment: Fragment(), StatisticsContract.View {

    private lateinit var statisticsPresenter: StatisticsPresenter

    override fun setPresenter(presenter: StatisticsPresenter) {
        statisticsPresenter = presenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statisticsPresenter.start()
    }

    override fun setProgressIndicator(active: Boolean) {
        if (active) {
            statistics.setText(R.string.loading)
        } else {
            statistics.text = ""
        }
    }

    override fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompletedTasks: Int) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            statistics.setText(R.string.statistics_no_tasks)
        } else {
            statistics.text = "${getString(R.string.statistics_active_tasks)} $numberOfIncompleteTasks \n " +
                    "${getString(R.string.statistics_completed_tasks)} $numberOfCompletedTasks"
        }
    }

    override fun setLoadingStatisticsError() {
        statistics.setText(R.string.statistics_error)
    }

    override fun isActive(): Boolean = isAdded

    companion object {
        fun newInstance(): StatisticsFragment = StatisticsFragment()
    }

}
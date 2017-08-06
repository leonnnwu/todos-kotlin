package todo.android.lwu.com.todos.tasks

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.BaseAdapter
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.tasks_fag.*
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.databinding.TaskItemBinding
import todo.android.lwu.com.todos.databinding.TasksFagBinding
import todo.android.lwu.com.todos.taskdetail.TaskDetailActivity

/**
 * Created by lwu on 4/23/17.
 */
class TasksFragment: Fragment(), TasksContract.View{

    private lateinit var presenter: TasksContract.Presenter
    private lateinit var listAdapter: TasksAdapter
    private var tasksViewModel: TasksViewModel? = null

    companion object {
        fun newInstance(): TasksFragment = TasksFragment()
    }

    override fun setPresenter(presenter: TasksContract.Presenter) {
        this.presenter = presenter
    }

    fun setTasksViewModel(viewModel: TasksViewModel) {
        tasksViewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val tasksFragBinding = TasksFagBinding.inflate(inflater, container, false)
        tasksFragBinding.tasks = tasksViewModel
        tasksFragBinding.actionHandler = presenter

        // Set up tasks view
        val listView = tasksFragBinding.tasksList

        listAdapter = TasksAdapter(arrayListOf<Task>(), presenter)
        listView.adapter = listAdapter

        with(tasksFragBinding.refreshLayout) {
            // Set up refresh progress indicator
            setColorSchemeColors(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorAccent),
                    ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )

            // Set the scrolling view in the custom swipeRefreshLayout
            setScrollUpChild(listView)

            setOnRefreshListener {
                presenter.loadTasks(false)
            }
        }

        setHasOptionsMenu(true)

        val root = tasksFragBinding.root

        return root
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear -> { presenter.clearCompletedTasks(); true }
            R.id.menu_filter -> { showFilteringPopUpMenu(); true }
            R.id.menu_fresh -> { presenter.loadTasks(true); true }
            else -> false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun showSuccessfullySavedMessage() {
        Snackbar.make(view!!, getString(R.string.successfully_saved_task_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showTaskMarkedComplete() {
        Snackbar.make(view!!, getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG).show()
    }

    override fun showTaskMarkedActivate() {
        Snackbar.make(view!!, getString(R.string.task_marked_active), Snackbar.LENGTH_LONG).show()
    }

    override fun showCompletedTaskClear() {
        Snackbar.make(view!!, getString(R.string.completed_tasks_cleared), Snackbar.LENGTH_LONG).show()
    }

    override fun showTasks(tasks: List<Task>) {
        listAdapter.replaceData(tasks)
        tasksViewModel?.setTaskListSize(tasks.size)
    }

    override fun showAddTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK)
    }

    private fun showFilteringPopUpMenu() {
        val popup = PopupMenu(context, activity.findViewById(R.id.menu_filter))
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.active -> presenter.setFiltering(TasksFilterType.ACTIVE_TASKS)
                R.id.completed -> presenter.setFiltering(TasksFilterType.COMPLETED_TASKS)
                else -> presenter.setFiltering(TasksFilterType.ALL_TASKS)
            }
            presenter.loadTasks(false)
            true
        }
        popup.show()
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    override fun showTaskDetail(taskId: String) {
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }

    override fun setLoadingIndicator(active: Boolean) {
        // Make sure setRefreshing is called after the layout is done with everything else.
        refresh_layout.post {
            refresh_layout.isRefreshing = active
        }
    }

    override fun showLoadingTasksError() {
        Snackbar.make(view!!, getString(R.string.loading_tasks_error), Snackbar.LENGTH_LONG).show()
    }

    private class TasksAdapter(tasks: List<Task>, val userActionsListener: TasksContract.Presenter): BaseAdapter() {

        val dataSet: MutableList<Task> = tasks.toMutableList()

        fun replaceData(newTasks: List<Task>) {
            setList(newTasks)
        }

        private fun setList(newTasks: List<Task>) {
            dataSet.clear()
            dataSet.addAll(newTasks)
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Task {
            return dataSet[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return dataSet.size
        }

        override fun getView(position: Int, contentView: View?, viewGroup: ViewGroup): View {
            val task = getItem(position)
            val binding: TaskItemBinding

            if (contentView == null) {
                binding = TaskItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            } else {
                binding = DataBindingUtil.getBinding(contentView)
            }

            val itemActionHandler = TasksItemActionHandler(userActionsListener)
            binding.actionHandler = itemActionHandler
            binding.task = task
            binding.executePendingBindings()
            return binding.root
        }
    }
}
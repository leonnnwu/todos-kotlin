package todo.android.lwu.com.todos.tasks

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.BaseAdapter
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.task_item.view.*
import kotlinx.android.synthetic.main.tasks_fag.*
import kotlinx.android.synthetic.main.tasks_fag.view.*
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.taskdetail.TaskDetailActivity

/**
 * Created by lwu on 4/23/17.
 */
class TasksFragment: Fragment(), TasksContract.View{

    private lateinit var presenter: TasksContract.Presenter
    private lateinit var listAdapter: TasksAdapter

    companion object {
        fun newInstance(): TasksFragment = TasksFragment()
    }

    override fun setPresenter(presenter: TasksContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = TasksAdapter(emptyList(), object: TasksAdapter.TaskItemListener {
            override fun onTaskClick(clickedTask: Task) {
                presenter.openTaskDetails(clickedTask)
            }

            override fun onCompletedTaskClick(completedTask: Task) {
                presenter.completeTask(completedTask)
            }

            override fun onActivateTaskClick(activatedTask: Task) {
                presenter.activateTask(activatedTask)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.tasks_fag, container, false)

        // Set up tasks view
        root.tasks_list.adapter = listAdapter

        // Set up no tasks view
        root.noTasksAdd.setOnClickListener { _ ->
            showAddTask()
        }

        with(root.refresh_layout) {
            // Set up refresh progress indicator
            setColorSchemeColors(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorAccent),
                    ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )

            // Set the scrolling view in the custom swipeRefreshLayout
            setScrollUpChild(root.tasks_list)

            setOnRefreshListener {
                presenter.loadTasks(false)
            }
        }

        setHasOptionsMenu(true)

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

        tasksLL.visibility = View.VISIBLE
        noTasks.visibility = View.GONE
    }

    override fun showAddTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK)
    }

    override fun showFilteringPopUpMenu() {
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

    override fun showActiveFilterLabel() {
        filteringLabel.text = resources.getString(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabel.text = resources.getString(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabel.text = resources.getString(R.string.label_all)
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

    override fun showNoTasks() {
        showNoTasksView(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        )
    }

    override fun showNoActiveTasks() {
        showNoTasksView(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_check_circle_24dp,
                false
        )

    }

    override fun showNoCompletedTasks() {
        showNoTasksView(
                resources.getString(R.string.no_tasks_all),
                R.drawable.ic_verified_user_24dp,
                false
        )
    }

    override fun showLoadingTasksError() {
        Snackbar.make(view!!, getString(R.string.loading_tasks_error), Snackbar.LENGTH_LONG).show()
    }

    private fun showNoTasksView(text: String, icon: Int, showAddView: Boolean) {
        tasksLL.visibility = View.GONE
        noTasks.visibility = View.VISIBLE
        noTasksMain.text = text
        noTasksIcon.setImageDrawable(ContextCompat.getDrawable(context, icon))
        noTasksAdd.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    private class TasksAdapter(tasks: List<Task>, val itemListener: TaskItemListener): BaseAdapter() {

        val dataSet: MutableList<Task> = tasks.toMutableList()

        fun replaceData(newTasks: List<Task>) {
            setList(newTasks)
            notifyDataSetChanged()
        }

        private fun setList(newTasks: List<Task>) {
            dataSet.clear()
            dataSet.addAll(newTasks)
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
            val rowView = contentView ?: LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item, viewGroup, false)

            val task = getItem(position)

            //Set TextView
            rowView.title.text = task.getTitleForList()

            //Set checkbox
            rowView.complete.isChecked = task.completed

            if (task.completed) {
                rowView.setBackgroundResource(R.drawable.list_completed_touch_feedback)
            } else {
                rowView.setBackgroundResource(R.drawable.touch_feedback)
            }

            //Bind listener
            rowView.complete.setOnClickListener {
                if (!task.completed) {
                    itemListener.onCompletedTaskClick(task)
                } else {
                    itemListener.onActivateTaskClick(task)
                }
            }

            rowView.setOnClickListener {
                itemListener.onTaskClick(task)
            }

            return rowView
        }

        interface TaskItemListener {

            fun onTaskClick(clickedTask: Task)

            fun onCompletedTaskClick(completedTask: Task)

            fun onActivateTaskClick(activatedTask: Task)
        }
    }
}
package todo.android.lwu.com.todos.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.task_item.view.*
import kotlinx.android.synthetic.main.tasks_fag.*
import kotlinx.android.synthetic.main.tasks_fag.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.events.TasksDownloadedEvent

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
        val view = inflater.inflate(R.layout.tasks_fag, container, false)

        // Set up tasks view
        view.tasks_list.adapter = listAdapter

        // Set up refresh progress indicator
        view.refresh_layout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
        )

        // Set the scrolling view in the custom swipeRefreshLayout
        view.refresh_layout.setScrollUpChild(view.tasks_list)
        view.refresh_layout.setOnRefreshListener { presenter.loadTasks(false) }

        // TODO: Add menu

        return view
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun showTasks(tasks: List<Task>) {
        listAdapter.replaceData(tasks)

        tasksLL.visibility = View.VISIBLE
        noTasks.visibility = View.GONE
    }

    override fun showNoTasks() {
        tasksLL.visibility = View.GONE
        noTasks.visibility = View.VISIBLE
        noTasksMain.text = context.getString(R.string.no_tasks_all)
        noTasksIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_assignment_turned_in_24dp))
        noTasksAdd.visibility = View.GONE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadAllTasks(event: TasksDownloadedEvent.All) {
        showTasks(event.taskList)
    }
}

class TasksAdapter(tasks: List<Task>, val itemListener: TaskItemListener): BaseAdapter() {

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
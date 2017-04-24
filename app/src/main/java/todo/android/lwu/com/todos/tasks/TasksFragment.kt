package todo.android.lwu.com.todos.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.tasks_fag.*
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 4/23/17.
 */
class TasksFragment(): Fragment(), TasksContract.View, TasksAdapter.TaskItemListener{

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
        listAdapter = TasksAdapter(emptyList(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tasks_fag, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set up tasks view
        tasks_list.adapter = listAdapter

        refresh_layout.setColorSchemeColors(
                ContextCompat.getColor(activity, R.color.colorPrimary),
                ContextCompat.getColor(activity, R.color.colorAccent),
                ContextCompat.getColor(activity, R.color.colorPrimaryDark)
        )

        // Set the scrolling view in the custom SwipeRefreshLayout
        refresh_layout.setScrollUpChild(tasks_list)

        refresh_layout.setOnRefreshListener {
            presenter.loadTasks(false)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onTaskClick(clickedTask: Task) {
        presenter.openTaskDetails(clickedTask)
    }

    override fun onCompletedTaskClick(completedTask: Task) {
        presenter.completeTask(completedTask)
    }

    override fun onActivateTaskClick(activatedTask: Task) {
        presenter.activateTask(activatedTask)
    }
}

class TasksAdapter(val tasks: List<Task>, val itemListener: TaskItemListener): BaseAdapter() {

    override fun getItem(position: Int): Task {
        return tasks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getView(position: Int, contentView: View?, viewGroup: ViewGroup): View {
        val rowView = contentView ?: LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item, viewGroup, false)

        val task = getItem(position)

        //Set TextView
        val titleTV = rowView.findViewById(R.id.title) as TextView
        titleTV.text = task.getTitleForList()

        //Set checkbox
        val completeCB = rowView.findViewById(R.id.complete) as CheckBox
        completeCB.isChecked = task.completed

        if (task.completed) {
            rowView.setBackgroundResource(R.drawable.list_completed_touch_feedback)
        } else {
            rowView.setBackgroundResource(R.drawable.touch_feedback)
        }

        //Bind listener
        completeCB.setOnClickListener {
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
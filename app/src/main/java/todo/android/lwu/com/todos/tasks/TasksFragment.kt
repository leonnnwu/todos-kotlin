package todo.android.lwu.com.todos.tasks

import android.os.Bundle
import android.support.v4.app.Fragment
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
        val root = inflater.inflate(R.layout.tasks_fag, container, false)

        //Set up tasks view
        tasks_list.adapter = listAdapter

        return root
    }

    override fun onTaskClick(clickedTask: Task) {
    }

    override fun onCompletedTaskClick(completedTask: Task) {
    }

    override fun onActivateTaskClick(activatedTask: Task) {
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
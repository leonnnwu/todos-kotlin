package todo.android.lwu.com.todos.taskdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.task_item.*
import kotlinx.android.synthetic.main.taskdetail_frag.*
import kotlinx.android.synthetic.main.taskdetail_frag.view.*
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity.Companion.REQUEST_EDIT_TASK

/**
 * Created by lwu on 6/25/17.
 */
class TaskDetailFragment: Fragment(), TaskDetailContract.View {

    private lateinit var taskDetailPresenter: TaskDetailContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.taskdetail_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.task_detail_complete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                taskDetailPresenter.completeTask()
            } else {
                taskDetailPresenter.activateTask()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity.findViewById(R.id.fab_edit_task).setOnClickListener {
            taskDetailPresenter.editTask()
        }
    }

    override fun onResume() {
        super.onResume()
        taskDetailPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        taskDetailPresenter.unsubscribe()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AddEditTaskActivity.REQUEST_EDIT_TASK && resultCode == Activity.RESULT_OK) {
            activity.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                taskDetailPresenter.deleteTask()
                true
            }
            else -> false
        }
    }

    override fun setPresenter(presenter: TaskDetailContract.Presenter) {
        taskDetailPresenter = presenter
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            task_detail_description.text = getString(R.string.loading)
        }
    }

    override fun showMissingTask() {
        task_detail_title.text = ""
        task_detail_description.text = getString(R.string.no_data)
    }

    override fun hideTitle() {
        task_detail_title.visibility = View.GONE
    }

    override fun showTitle(title: String) {
        task_detail_title.visibility = View.VISIBLE
        task_detail_title.text = title
    }

    override fun hideDescription() {
        task_detail_description.visibility = View.GONE
    }

    override fun showDescription(description: String) {
        task_detail_description.visibility = View.VISIBLE
        task_detail_description.text = description
    }

    override fun showCompletionStatus(complete: Boolean) {
        task_detail_complete.isChecked = complete
    }

    override fun showEditTask(taskId: String) {
        val intent = Intent(context, AddEditTaskActivity::class.java).apply {
            putExtra(AddEditTaskActivity.ARGUMENT_EDIT_TASK_ID, taskId)
        }
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_EDIT_TASK)
    }

    override fun showTaskDeleted() {
        activity.finish()
    }

    override fun showTaskMarkedComplete() {
        Snackbar.make(view!!, getString(R.string.task_marked_complete), Snackbar.LENGTH_LONG).show()
    }

    override fun showTaskMarkedActive() {
        Snackbar.make(view!!, getString(R.string.task_marked_active), Snackbar.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean = isAdded

    companion object {

        const val ARGUMENT_TASK_ID = "ARGUMENT_TASK_ID"

        fun newInstance(): TaskDetailFragment = TaskDetailFragment()

        fun newInstance(taskId: String): TaskDetailFragment {
            return TaskDetailFragment().apply {
                arguments = Bundle().apply {
                    putCharSequence(ARGUMENT_TASK_ID, taskId)
                }
            }
        }
    }
}
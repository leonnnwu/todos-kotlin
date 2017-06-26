package todo.android.lwu.com.todos.addedittask

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.addtask_act.*
import kotlinx.android.synthetic.main.addtask_act.view.*
import kotlinx.android.synthetic.main.addtask_frag.*
import kotlinx.android.synthetic.main.task_item.*
import todo.android.lwu.com.todos.R


/**
 * Created by lwu on 6/24/17.
 */
class AddEditTaskFragment: Fragment(), AddEditTaskContract.View {

    private lateinit var presenter: AddEditTaskContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.addtask_frag, container, false)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity.findViewById(R.id.fab_edit_task_done).setOnClickListener {
            presenter.saveTask(add_task_title.text.toString(), add_task_description.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setPresenter(presenter: AddEditTaskContract.Presenter) {
        this.presenter = presenter
    }

    override fun showEmptyTaskError() {
        Snackbar.make(view!!, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showTasksList() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun setTitle(title: String) {
        add_task_title.setText(title)
    }

    override fun setDescription(description: String) {
        add_task_description.setText(description)
    }

    override fun isActive(): Boolean = isAdded

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"

        fun newInstance(taskId: String?): AddEditTaskFragment {
            return AddEditTaskFragment().apply {
                arguments = Bundle().apply {
                    this.putCharSequence(ARGUMENT_EDIT_TASK_ID, taskId)
                }
            }
        }
    }
}
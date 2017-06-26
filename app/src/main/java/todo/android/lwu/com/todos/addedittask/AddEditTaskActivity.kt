package todo.android.lwu.com.todos.addedittask

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.addtask_act.*
import todo.android.lwu.com.todos.Injection
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.utils.addFragmentToActivity

/**
 * Created by lwu on 6/24/17.
 */
class AddEditTaskActivity: AppCompatActivity() {

    private lateinit var presenter: AddEditTaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_act)

        // Set up the toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val taskId = intent.getStringExtra(ARGUMENT_EDIT_TASK_ID)
        supportActionBar?.setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)

        val addEditTaskFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as? AddEditTaskFragment ?: AddEditTaskFragment.newInstance(taskId)

        if (!addEditTaskFragment.isAdded) {
            addFragmentToActivity(supportFragmentManager, addEditTaskFragment, R.id.contentFrame)
        }

        val shouldLoadDataFromRepo = savedInstanceState?.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY) ?: true

        presenter = AddEditTaskPresenter(
                taskId,
                Injection.provideTasksRepository(applicationContext),
                addEditTaskFragment,
                shouldLoadDataFromRepo)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, presenter.isDataMissing())
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val REQUEST_ADD_TASK = 1
        const val REQUEST_EDIT_TASK = 2
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"
    }
}
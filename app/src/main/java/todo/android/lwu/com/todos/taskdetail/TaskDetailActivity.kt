package todo.android.lwu.com.todos.taskdetail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.taskdetail_act.*
import todo.android.lwu.com.todos.Injection
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.utils.addFragmentToActivity

/**
 * Created by lwu on 6/25/17.
 */
class TaskDetailActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskdetail_act)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)

        val taskDetailFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as? TaskDetailFragment ?: TaskDetailFragment.newInstance()

        if (!taskDetailFragment.isAdded) {
            addFragmentToActivity(supportFragmentManager, taskDetailFragment, R.id.contentFrame)
        }

        TaskDetailPresenter(taskId, taskDetailFragment, Injection.provideTasksRepository(applicationContext))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
package todo.android.lwu.com.todos.tasks

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.tasks_activity.*
import timber.log.Timber
import todo.android.lwu.com.todos.Injection
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.utils.addFragmentToActivity

class TasksActivity : AppCompatActivity() {

    private lateinit var tasksPresenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize Timber log library
        Timber.plant(Timber.DebugTree())

        //Set the content view
        setContentView(R.layout.tasks_activity)

        //Set up the toolbar
        setSupportActionBar(toolbar)

        //Setup the navigation drawer
        setupDrawerContent(nav_view)

        //Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //Add tasks fragment to activity
        val tasksFragment: TasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment? ?: TasksFragment.newInstance()
        tasksFragment.takeIf { !it.isAdded }?.let { addFragmentToActivity(supportFragmentManager, it, R.id.contentFrame) }

        //Create the presenter
        tasksPresenter = TasksPresenter(Injection.provideTasksRepository(applicationContext), tasksFragment)

        //Set listener of floating action bar.
        fab_add_task.setOnClickListener { _ ->
            tasksPresenter.addNewTask()
        }

        if (savedInstanceState != null) {
            tasksPresenter.setFiltering(savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CURRENT_FILTERING_KEY, tasksPresenter.getFiltering())
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> Timber.d("to-do list was clicked!")
                R.id.statistics_navigation_menu_item -> Timber.d("statistics was clicked!")
                else -> Unit
            }

            //Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }

    companion object {
        private const val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"
    }
}

package todo.android.lwu.com.todos.tasks

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.tasks_activity.*
import timber.log.Timber
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.data.source.TasksRepository
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
        drawer_layout.setStatusBarBackgroundColor(R.color.colorPrimaryDark)
        if (nav_view != null) {
            setupDrawerContent(nav_view)
        }

        //Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //Set listener of floating action bar.
        fab_add_task.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        //Add tasks fragment to activity
        val tasksFragment: TasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment? ?: TasksFragment.newInstance()
        addFragmentToActivity(supportFragmentManager, tasksFragment, R.id.contentFrame)

        //Create the presenter
        //FIXME: Create TasksPresenter that contains data repository
        tasksPresenter = TasksPresenter(TasksRepository, tasksFragment)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
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
}

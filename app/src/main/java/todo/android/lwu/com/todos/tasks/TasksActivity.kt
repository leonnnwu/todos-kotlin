package todo.android.lwu.com.todos.tasks

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_tasks.*
import kotlinx.android.synthetic.main.app_bar_tasks.*
import timber.log.Timber
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.data.source.TasksRepository
import todo.android.lwu.com.todos.utils.addFragmentToActivity

class TasksActivity : AppCompatActivity() {

    private lateinit var tasksPresenter: TasksPresenter

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize Timber log library
        Timber.plant(Timber.DebugTree())

        //Set the content view
        setContentView(R.layout.activity_tasks)

        //Set up the toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //Set listener of floating action bar.
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        //Setup the navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerLayout.setStatusBarBackgroundColor(R.color.colorPrimaryDark)
        if (nav_view != null) {
            setupDrawerContent(nav_view)
        }

        //Add tasks fragment to activity
        val tasksFragment: TasksFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as TasksFragment? ?: TasksFragment.newInstance()
        addFragmentToActivity(supportFragmentManager, tasksFragment, R.id.contentFrame)

        //Create the presenter
        //FIXME: Create TasksPresenter that contains data repository
        tasksPresenter = TasksPresenter(TasksRepository(), tasksFragment)

        //FIXME: Load previously saved state, if available

        //Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.list_navigation_menu_item -> Timber.d("to-do list was clicked!")
                R.id.statistics_navigation_menu_item -> Timber.d("statistics was clicked!")
            }

            //Close the navigation drawer when an item is selected.
            it.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }
}

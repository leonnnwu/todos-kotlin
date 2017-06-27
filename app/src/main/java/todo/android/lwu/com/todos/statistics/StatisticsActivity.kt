package todo.android.lwu.com.todos.statistics

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.statistics_act.*
import todo.android.lwu.com.todos.Injection
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.utils.addFragmentToActivity

/**
 * Created by lwu on 6/26/17.
 */
class StatisticsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.statistics_act)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.statistics_title)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawer_layout.setStatusBarBackground(R.color.colorPrimaryDark)
        setDrawerContent(nav_view)

        val statisticsFragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as? StatisticsFragment
                ?: StatisticsFragment.newInstance()

        if (!statisticsFragment.isAdded) {
            addFragmentToActivity(supportFragmentManager, statisticsFragment, R.id.contentFrame)
        }

        StatisticsPresenter(Injection.provideTasksRepository(applicationContext), statisticsFragment)
    }

    private fun setDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    NavUtils.navigateUpFromSameTask(this)
                }

                R.id.statistics_navigation_menu_item -> Unit
                else -> Unit
            }

            menuItem.isChecked = true
            drawer_layout.closeDrawers()
            true
        }
    }
}
package todo.android.lwu.com.todos.taskdetail

import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import todo.android.lwu.com.todos.R
import todo.android.lwu.com.todos.TestUtils
import todo.android.lwu.com.todos.data.FakeTasksRemoteDataSource
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 7/3/17.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TaskDetailScreenTest {

    companion object {
        private const val TASK_TITLE = "ATSL"
        private const val TASK_DESCRIPTION = "Rocks"

        private val ACTIVE_TASK = Task(title = TASK_TITLE, description = TASK_DESCRIPTION, completed = false)
        private val COMPLETED_TASK = Task(title = TASK_TITLE, description = TASK_DESCRIPTION, completed = true)
    }

    @Rule
    @JvmField val taskDetailActivityTestRule = ActivityTestRule(TaskDetailActivity::class.java, true, false)

    @Test
    fun activeTaskDetails_DisplayedInUi() {
        loadActiveTask()

        Espresso.onView(ViewMatchers.withId(R.id.task_detail_title)).check(ViewAssertions.matches(ViewMatchers.withText(TASK_TITLE)))
        Espresso.onView(ViewMatchers.withId(R.id.task_detail_description)).check(ViewAssertions.matches(ViewMatchers.withText(TASK_DESCRIPTION)))
        Espresso.onView(ViewMatchers.withId(R.id.task_detail_complete)).check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
    }

    @Test
    fun completedTaskDetails_DisplayedInUi() {
        loadCompletedTask()

        Espresso.onView(ViewMatchers.withId(R.id.task_detail_title)).check(ViewAssertions.matches(ViewMatchers.withText(TASK_TITLE)))
        Espresso.onView(ViewMatchers.withId(R.id.task_detail_description)).check(ViewAssertions.matches(ViewMatchers.withText(TASK_DESCRIPTION)))
        Espresso.onView(ViewMatchers.withId(R.id.task_detail_complete)).check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun orientationChange_menuAndTaskPersist() {
        loadActiveTask()

        Espresso.onView(ViewMatchers.withId(R.id.menu_delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        TestUtils.rotateOrientation(taskDetailActivityTestRule.activity)

        Espresso.onView(ViewMatchers.withId(R.id.task_detail_title)).check(ViewAssertions.matches(ViewMatchers.withText(TASK_TITLE)))
        Espresso.onView(ViewMatchers.withId(R.id.task_detail_description)).check(ViewAssertions.matches(ViewMatchers.withText(TASK_DESCRIPTION)))

        Espresso.onView(ViewMatchers.withId(R.id.menu_delete)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun loadActiveTask() {
        startActivityWithStubbedTask(ACTIVE_TASK)
    }

    private fun loadCompletedTask() {
        startActivityWithStubbedTask(COMPLETED_TASK)
    }

    private fun startActivityWithStubbedTask(task: Task) {
        TasksRepository.destroyInstance()
        FakeTasksRemoteDataSource.saveTask(task)

        val startIntent = Intent().apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
        }

        taskDetailActivityTestRule.launchActivity(startIntent)
    }
}
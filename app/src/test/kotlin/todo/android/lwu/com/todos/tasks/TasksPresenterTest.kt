package todo.android.lwu.com.todos.tasks

import android.app.Activity
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import todo.android.lwu.com.todos.addedittask.AddEditTaskActivity
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 7/2/17.
 *
 * Unit tests for the implementation of {@link TasksPresenter}
 */
class TasksPresenterTest {

    private lateinit var tasks: List<Task>

    @Mock
    private lateinit var tasksRepository: TasksRepository

    @Mock
    private lateinit var tasksView: TasksContract.View

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    private lateinit var loadTasksCallbackCaptor: KArgumentCaptor<TasksDataSource.LoadTasksCallback>

    private lateinit var tasksPresenter: TasksPresenter

    @Before
    fun setupTasksPresenter() {

        // Mockito has a very convenient way to inject mocks by using the @Mock annotation.
        // To inject the mocks in the test initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        tasksPresenter = TasksPresenter(tasksRepository, tasksView)

        loadTasksCallbackCaptor = argumentCaptor<TasksDataSource.LoadTasksCallback>()

        // The presenter won't update the view unless it's active
        whenever(tasksView.isActive()).thenReturn(true)

        tasks = listOf(
                Task(title = "Title1", description = "Description1"),
                Task(title = "Title2", description = "Description2", completed = true),
                Task(title = "Title3", description = "Description3", completed = true)
        )
    }

    @Test
    fun createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test
        tasksPresenter = TasksPresenter(tasksRepository, tasksView)

        // Then the presenter is set to the view
        verify(tasksView).setPresenter(tasksPresenter)
    }

    @Test
    fun loadAllTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksPresenter with initialized tasks
        // When loading of Tasks is requested
        tasksPresenter.setFiltering(TasksFilterType.ALL_TASKS)
        tasksPresenter.loadTasks(true)

        // Callback is captured and invoked with stubbed tasks
        verify(tasksRepository).getAllTasks(loadTasksCallbackCaptor.capture())

        loadTasksCallbackCaptor.firstValue.onTasksLoaded(tasks)

        // Then progress indicator is shown
        inOrder(tasksView).apply {
            verify(tasksView).setLoadingIndicator(true)
            // Then progress indicator is hidden and all tasks are shown in UI
            verify(tasksView).setLoadingIndicator(false)
        }

        argumentCaptor<List<Task>>().apply {

            verify(tasksView).showTasks(capture())

            Assert.assertEquals(3, firstValue.size)
        }

    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView() {
        tasksPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS)
        tasksPresenter.loadTasks(true)

        verify(tasksRepository).getAllTasks(loadTasksCallbackCaptor.capture())
        loadTasksCallbackCaptor.firstValue.onTasksLoaded(tasks)

        inOrder(tasksView).apply {
            verify(tasksView).setLoadingIndicator(true)
            verify(tasksView).setLoadingIndicator(false)
        }

        argumentCaptor<List<Task>>().apply {
            verify(tasksView).showTasks(capture())

            Assert.assertEquals(1, firstValue.size)
        }

    }

    @Test
    fun loadCompleteTasksFromRepositoryAndLoadIntoView() {
        tasksPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS)
        tasksPresenter.loadTasks(true)

        verify(tasksRepository).getAllTasks(loadTasksCallbackCaptor.capture())
        loadTasksCallbackCaptor.firstValue.onTasksLoaded(tasks)

        inOrder(tasksView).apply {
            verify(tasksView).setLoadingIndicator(true)
            verify(tasksView).setLoadingIndicator(false)
        }

        argumentCaptor<List<Task>>().apply {
            verify(tasksView).showTasks(capture())

            Assert.assertEquals(2, firstValue.size)
        }

    }

    @Test
    fun clickOnFab_ShowsAddTaskUi() {
        tasksPresenter.addNewTask()

        verify(tasksView).showAddTask()
    }

    @Test
    fun clickOnTask_ShowDetailUi() {
        val requestedTask = Task(title = "Details Requested", description = "For this task")

        tasksPresenter.openTaskDetails(requestedTask)

        verify(tasksView).showTaskDetail(any<String>())
    }

    @Test
    fun completeTask_ShowTaskMarkedComplete() {
        val requestedTask = Task(title = "Details Requested", description = "For this task")

        tasksPresenter.completeTask(requestedTask)

        verify(tasksRepository).completeTask(requestedTask)
        verify(tasksView).showTaskMarkedComplete()
    }

    @Test
    fun activateTask_ShowTaskMarkedComplete() {
        val requestedTask = Task(title = "Details Requested", description = "For this task")

        tasksPresenter.activateTask(requestedTask)

        verify(tasksRepository).activateTask(requestedTask)
        verify(tasksView).showTaskMarkedActivate()
    }

    @Test
    fun unavailableTasks_ShowsError() {
        tasksPresenter.setFiltering(TasksFilterType.ALL_TASKS)
        tasksPresenter.loadTasks(true)

        verify(tasksRepository).getAllTasks(loadTasksCallbackCaptor.capture())
        loadTasksCallbackCaptor.firstValue.onDataNotAvailable()

        verify(tasksView).showLoadingTasksError()
    }

    @Test
    fun clearCompletedTask() {
        tasksPresenter.clearCompletedTasks()
        verify(tasksRepository).clearCompletedTasks()
        verify(tasksView).showCompletedTaskClear()

        verify(tasksRepository).getAllTasks(loadTasksCallbackCaptor.capture())
    }

    @Test
    fun testResult() {
        tasksPresenter.result(AddEditTaskActivity.REQUEST_ADD_TASK, Activity.RESULT_OK)
        verify(tasksView).showSuccessfullySavedMessage()
    }
}
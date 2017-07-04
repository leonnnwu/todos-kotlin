package todo.android.lwu.com.todos.data.source

import android.content.Context
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import todo.android.lwu.com.todos.data.Task

/**
 * Created by lwu on 7/3/17.
 */
class TasksRepositoryTest {

    companion object {
        private const val TASK_TITLE = "title"
        private const val TASK_TITLE2 = "title2"
        private const val TASK_TITLE3 = "title3"

        private val TASKS = listOf<Task>(Task(title = "Title1", description = "Description1"),
                Task(title = "Title2", description = "Description2"))
    }

    @Mock
    private lateinit var tasksRemoteDataSource: TasksDataSource

    @Mock
    private lateinit var tasksLocalDataSource: TasksDataSource

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var getTaskCallback: TasksDataSource.GetTaskCallback

    @Mock
    private lateinit var loadTasksCallback: TasksDataSource.LoadTasksCallback

    private lateinit var tasksRepository: TasksRepository

    private lateinit var allTasksCallbackCaptor: KArgumentCaptor<TasksDataSource.LoadTasksCallback>

    private lateinit var taskCallbackCaptor: KArgumentCaptor<TasksDataSource.GetTaskCallback>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        allTasksCallbackCaptor = argumentCaptor()
        taskCallbackCaptor = argumentCaptor()

        tasksRepository = TasksRepository.getInstance(tasksRemoteDataSource, tasksLocalDataSource)
    }

    @After
    fun destroyRepositoryInstance() {
        TasksRepository.destroyInstance()
    }

    @Test
    fun getTasks_repositoryCachesAfterFirstApiCall() {
        twoTasksLoadCallsToRepository(loadTasksCallback)

        verify(tasksRemoteDataSource).getAllTasks(any<TasksDataSource.LoadTasksCallback>())
    }

    @Test
    fun getTasks_requestAllTasksFromLocalDataSource() {
        tasksRepository.getAllTasks(loadTasksCallback)

        verify(tasksLocalDataSource).getAllTasks(any<TasksDataSource.LoadTasksCallback>())

    }

    @Test
    fun saveTask_savesTaskToServiceAPI() {
        val newTask = Task(title = TASK_TITLE, description = "Some Task Description")

        tasksRepository.saveTask(newTask)

        verify(tasksRemoteDataSource).saveTask(newTask)
        verify(tasksLocalDataSource).saveTask(newTask)
        Assert.assertEquals(tasksRepository.cachedTasks.size, 1)
    }

    private fun twoTasksLoadCallsToRepository(callback: TasksDataSource.LoadTasksCallback) {
        tasksRepository.getAllTasks(callback)

        verify(tasksLocalDataSource).getAllTasks(allTasksCallbackCaptor.capture())

        allTasksCallbackCaptor.firstValue.onDataNotAvailable()

        verify(tasksRemoteDataSource).getAllTasks(allTasksCallbackCaptor.capture())

        allTasksCallbackCaptor.firstValue.onTasksLoaded(TASKS)

        tasksRepository.getAllTasks(callback)
    }
}
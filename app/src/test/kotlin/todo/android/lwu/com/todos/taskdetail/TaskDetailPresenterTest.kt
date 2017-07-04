package todo.android.lwu.com.todos.taskdetail

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import todo.android.lwu.com.todos.data.Task
import todo.android.lwu.com.todos.data.source.TasksDataSource
import todo.android.lwu.com.todos.data.source.TasksRepository

/**
 * Created by lwu on 7/3/17.
 */
class TaskDetailPresenterTest {

    companion object {
        private const val TITLE_TEST = "title"
        private const val DESCRIPTION_TEST = "description"
        private const val INVALID_TASK_ID = ""
    }

    @Mock
    private lateinit var taskDetailView: TaskDetailContract.View

    @Mock
    private lateinit var taskRepository: TasksRepository

    private lateinit var getTaskCallbackCaptor: KArgumentCaptor<TasksDataSource.GetTaskCallback>

    private lateinit var taskDetailPresenter: TaskDetailPresenter

    private lateinit var ACTIVE_TASK: Task
    private lateinit var COMPLETED_TASK: Task


    @Before
    fun setup() {
        ACTIVE_TASK = Task(title = TITLE_TEST, description = DESCRIPTION_TEST)
        COMPLETED_TASK = Task(title = TITLE_TEST, description = DESCRIPTION_TEST, completed = true)

        MockitoAnnotations.initMocks(this)

        getTaskCallbackCaptor = argumentCaptor<TasksDataSource.GetTaskCallback>()

        whenever(taskDetailView.isActive()).thenReturn(true)
    }

    @Test
    fun createPresenter_setsThePresenterToView() {
        taskDetailPresenter = TaskDetailPresenter(ACTIVE_TASK.id, taskDetailView, taskRepository)

        verify(taskDetailView).setPresenter(taskDetailPresenter)
    }

    @Test
    fun getActiveTaskFromRepositoryAndLoadIntoView() {
        taskDetailPresenter = TaskDetailPresenter(ACTIVE_TASK.id, taskDetailView, taskRepository)
        taskDetailPresenter.start()

        val inOrder = inOrder(taskDetailView)

        inOrder.verify(taskDetailView).setLoadingIndicator(true)

        verify(taskRepository).getTask(eq(ACTIVE_TASK.id), getTaskCallbackCaptor.capture())
        getTaskCallbackCaptor.firstValue.onTaskLoaded(ACTIVE_TASK)

        inOrder.verify(taskDetailView).setLoadingIndicator(false)
        inOrder.verify(taskDetailView).showCompletionStatus(ACTIVE_TASK.completed)
        inOrder.verify(taskDetailView).showTitle(ACTIVE_TASK.title)
        inOrder.verify(taskDetailView).showDescription(ACTIVE_TASK.description)
    }

    @Test
    fun getCompletedTaskFromRepositoryAndLoadIntoView() {
        taskDetailPresenter = TaskDetailPresenter(COMPLETED_TASK.id, taskDetailView, taskRepository)
        taskDetailPresenter.start()

        val inOrder = inOrder(taskDetailView)

        inOrder.verify(taskDetailView).setLoadingIndicator(true)

        verify(taskRepository).getTask(eq(COMPLETED_TASK.id), getTaskCallbackCaptor.capture())
        getTaskCallbackCaptor.firstValue.onTaskLoaded(COMPLETED_TASK)

        inOrder.verify(taskDetailView).setLoadingIndicator(false)
        inOrder.verify(taskDetailView).showCompletionStatus(COMPLETED_TASK.completed)
        inOrder.verify(taskDetailView).showTitle(COMPLETED_TASK.title)
        inOrder.verify(taskDetailView).showDescription(COMPLETED_TASK.description)
    }

    @Test
    fun getUnknownTaskFromRepositoryAndLoadIntoView() {
        taskDetailPresenter = TaskDetailPresenter(INVALID_TASK_ID, taskDetailView, taskRepository)
        taskDetailPresenter.start()

        val inOrder = inOrder(taskDetailView)

        inOrder.verify(taskDetailView).setLoadingIndicator(true)

        verify(taskRepository).getTask(eq(INVALID_TASK_ID), getTaskCallbackCaptor.capture())
        getTaskCallbackCaptor.firstValue.onDataNotAvailable()

        verify(taskDetailView).showMissingTask()
    }

    @Test
    fun deleteTask() {
        val task = Task(title = TITLE_TEST, description = DESCRIPTION_TEST)

        taskDetailPresenter = TaskDetailPresenter(task.id, taskDetailView, taskRepository)
        taskDetailPresenter.deleteTask()

        verify(taskRepository).deleteTask(task.id)
        verify(taskDetailView).showTaskDeleted()
    }

    @Test
    fun completeTask() {
        val task = Task(title = TITLE_TEST, description = DESCRIPTION_TEST)

        taskDetailPresenter = TaskDetailPresenter(task.id, taskDetailView, taskRepository)
        taskDetailPresenter.completeTask()

        verify(taskRepository).completeTask(task.id)
        verify(taskDetailView).showTaskMarkedComplete()
    }

    @Test
    fun activateTask() {
        val task = Task(title = TITLE_TEST, description = DESCRIPTION_TEST)

        taskDetailPresenter = TaskDetailPresenter(task.id, taskDetailView, taskRepository)
        taskDetailPresenter.activateTask()

        verify(taskRepository).activateTask(task.id)
        verify(taskDetailView).showTaskMarkedActive()
    }

    @Test
    fun editTask() {
        val task = Task(title = TITLE_TEST, description = DESCRIPTION_TEST)

        taskDetailPresenter = TaskDetailPresenter(task.id, taskDetailView, taskRepository)
        taskDetailPresenter.editTask()

        verify(taskDetailView).showEditTask(task.id)
    }

    @Test
    fun invalidTaskIsNotShownWhenEditing() {
        taskDetailPresenter = TaskDetailPresenter(INVALID_TASK_ID, taskDetailView, taskRepository)
        taskDetailPresenter.editTask()

        // Then the edit mode is never started
        verify(taskDetailView, never()).showEditTask(INVALID_TASK_ID)
        verify(taskDetailView).showMissingTask()
    }
}
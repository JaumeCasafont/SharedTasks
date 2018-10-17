package com.jcr.sharedtasks.ui.list

import androidx.databinding.DataBindingComponent
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentBindingAdapters
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.testing.SingleFragmentActivity
import com.jcr.sharedtasks.util.DataBindingIdlingResourceRule
import com.jcr.sharedtasks.util.RecyclerViewMatcher
import com.jcr.sharedtasks.util.TaskExecutorWithIdlingResourceRule
import com.jcr.sharedtasks.util.ViewModelUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

class TasksListFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private val tasksLiveData = MutableLiveData<List<Task>>()
    private val projectReferenceLiveData = MutableLiveData<ProjectReference>()
    private lateinit var viewModel: TasksListViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private var tasksListFragment = TasksListFragmentTestClass.create("projectUUID")

    @Before
    fun init() {
        viewModel = Mockito.mock(TasksListViewModel::class.java)
        mockBindingAdapter = Mockito.mock(FragmentBindingAdapters::class.java)
        Mockito.doNothing().`when`(viewModel).setProjectUUID(ArgumentMatchers.anyString())
        Mockito.`when`(viewModel.tasks).thenReturn(tasksLiveData)
        Mockito.`when`(viewModel.projectReference).thenReturn(projectReferenceLiveData)
        tasksListFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        tasksListFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(tasksListFragment)
    }

    @Test
    fun loadTasksTest() {
        tasksLiveData.postValue(createTasks())
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(29)
        onView(withId(R.id.tasks_list_rv)).perform(action)
        onView(listMatcher().atPosition(29)).check(matches(isDisplayed()))
    }

    private fun createTasks(): List<Task> {
        return (0 until 30).map {
            createTask("task" + it.toString())
        }
    }

    private fun createTask(taskSID: String) = Task(taskSID, "taskTitle")

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.tasks_list_rv)
    }
}
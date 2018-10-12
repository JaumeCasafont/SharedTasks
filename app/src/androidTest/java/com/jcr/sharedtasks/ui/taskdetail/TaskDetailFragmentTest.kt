package com.jcr.sharedtasks.ui.taskdetail

import android.arch.lifecycle.MutableLiveData
import android.databinding.DataBindingComponent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentBindingAdapters
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.testing.SingleFragmentActivity
import com.jcr.sharedtasks.util.DataBindingIdlingResourceRule
import com.jcr.sharedtasks.util.TaskExecutorWithIdlingResourceRule
import com.jcr.sharedtasks.util.ViewModelUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private val taskLiveData = MutableLiveData<Task>()
    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val taskDetailFragment = TaskDetailFragment.create("taskSID")

    @Before
    fun init() {
        viewModel = mock(TaskDetailViewModel::class.java)
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)
        doNothing().`when`(viewModel).setTaskSID(ArgumentMatchers.anyString())
        `when`(viewModel.task).thenReturn(taskLiveData)
        taskDetailFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        taskDetailFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(taskDetailFragment)
    }

    @Test
    fun showTaskTitleAndDescriptionWhenIsLoaded() {
        val task = Task("taskSID", 1L, "task_title",
                "assignee", "description",
                0, false, 0, true)
        `when`(viewModel.getTaskToUpload()).thenReturn(task)
        taskLiveData.postValue(task)

        onView(withId(R.id.task_title_et)).check(
                ViewAssertions.matches(
                        ViewMatchers.withText("task_title")
                )
        )

        onView(withId(R.id.task_description_et)).check(
                ViewAssertions.matches(
                        ViewMatchers.withText("description")
                )
        )
    }

    @Test
    fun saveTaskWhenSaveClick() {
        onView(withId(R.id.task_title_et))
                .perform(typeText("new_title"))

        onView(withId(R.id.task_description_et))
                .perform(typeText("new_description"))

        onView(withId(R.id.save_task)).perform(click())

        verify(viewModel).saveTask("new_title", "new_description")
    }



}
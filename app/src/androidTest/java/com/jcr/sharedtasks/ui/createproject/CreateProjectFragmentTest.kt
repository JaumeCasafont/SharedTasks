package com.jcr.sharedtasks.ui.createproject

import androidx.databinding.DataBindingComponent
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentBindingAdapters
import com.jcr.sharedtasks.testing.SingleFragmentActivity
import com.jcr.sharedtasks.ui.common.NavigationController
import com.jcr.sharedtasks.util.DataBindingIdlingResourceRule
import com.jcr.sharedtasks.util.TaskExecutorWithIdlingResourceRule
import com.jcr.sharedtasks.util.ViewModelUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class CreateProjectFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var viewModel: CreateProjectViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var navigationController: NavigationController

    private val createProjectFragment = CreateProjectFragment()

    @Before
    fun init() {
        viewModel = mock(CreateProjectViewModel::class.java)
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)
        createProjectFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        navigationController = mock(NavigationController::class.java)
        createProjectFragment.navigationController = navigationController
        createProjectFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(createProjectFragment)
    }

    @Test
    fun testCreateNewProjectAndNavigateToTasksList() {
        `when`(viewModel.createProject("ProjectName")).thenReturn("ProjectUUID")

        onView(withId(R.id.project_name_et)).perform(
                ViewActions.typeText("ProjectName")
        )

        closeSoftKeyboard()

        onView(withId(R.id.create_project_btn)).perform(click())

        verify(viewModel).createProject("ProjectName")
        verify(navigationController).navigateToTasksList("ProjectUUID", true)
    }

}
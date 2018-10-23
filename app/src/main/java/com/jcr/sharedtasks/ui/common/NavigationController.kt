package com.jcr.sharedtasks.ui.common

import androidx.fragment.app.FragmentManager

import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.ui.MainActivity
import com.jcr.sharedtasks.ui.createproject.CreateProjectFragment
import com.jcr.sharedtasks.ui.list.TasksListFragment
import com.jcr.sharedtasks.ui.taskdetail.TaskDetailFragment

import javax.inject.Inject

class NavigationController @Inject
constructor(activity: MainActivity) {
    private val containerId: Int = R.id.container
    private val fragmentManager: FragmentManager = activity.supportFragmentManager

    @JvmOverloads
    fun navigateToTasksList(projectUUID: String, popBackStack: Boolean = false) {
        if (popBackStack) fragmentManager.popBackStack()
        val tasksListFragment = TasksListFragment.create(projectUUID)
        fragmentManager.beginTransaction()
                .replace(containerId, tasksListFragment)
                .commit()
    }

    fun navigateToTaskDetail(taskSID: String?) {
        val taskDetailFragment = TaskDetailFragment.create(taskSID)
        val tag = "task/$taskSID"
        fragmentManager.beginTransaction()
                .replace(containerId, taskDetailFragment, tag)
                .addToBackStack(null)
                .commit()
    }

    fun navigateToCreateProject() {
        val createProjectFragment = CreateProjectFragment()
        val tag = "createProject"
        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentManager.beginTransaction()
                    .replace(containerId, createProjectFragment, tag)
                    .addToBackStack(null)
                    .commit()
        }
    }
}

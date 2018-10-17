package com.jcr.sharedtasks.ui.list

import android.os.Bundle

class TasksListFragmentTestClass : TasksListFragment() {

    override fun initWidget() {
    }

    companion object {
        private const val PROJECT_UUID_KEY = "projectUUID"

        @JvmStatic
        fun create(projectUUID: String?): TasksListFragmentTestClass {
            val tasksListFragment = TasksListFragmentTestClass()
            val args = Bundle()
            args.putString(PROJECT_UUID_KEY, projectUUID)
            tasksListFragment.arguments = args
            return tasksListFragment
        }
    }
}

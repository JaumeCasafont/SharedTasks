package com.jcr.sharedtasks.util

import com.jcr.sharedtasks.model.Task

object TestUtil {

    fun createTasks(count: Int): List<Task> {
        return (0 until count).map {
            Task("taskSID$it", "taskName$it")
        }
    }
}
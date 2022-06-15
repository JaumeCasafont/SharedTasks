package com.jcr.sharedtasks.db

import androidx.room.Database
import androidx.room.RoomDatabase

import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task

@Database(
        entities = [
            Task::class,
            ProjectReference::class],
        version = 2,
        exportSchema = false)
abstract class SharedTasksDb : RoomDatabase() {

    abstract fun tasksDao(): ProjectsDao
}

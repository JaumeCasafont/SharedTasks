package com.jcr.sharedtasks.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task

@Dao
abstract class ProjectsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProjectsReference(projectReference: ProjectReference)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTasks(tasks: List<Task>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTask(task: Task)

    @Query("SELECT * FROM ProjectReference")
    abstract fun loadProjectsReferences(): LiveData<List<ProjectReference>>

    @Query("SELECT * FROM ProjectReference " + "WHERE projectUUID = :projectUUID")
    abstract fun loadProjectReferenceById(projectUUID: String): LiveData<ProjectReference>

    @Query("SELECT * FROM task " + "WHERE taskProjectUUID = :projectUUID ")
    abstract fun loadTasks(projectUUID: String): LiveData<List<Task>>

    @Query("SELECT * FROM task " + "WHERE assignee = :assignee AND state < 2")
    abstract fun loadMyTasks(assignee: String): LiveData<List<Task>>

    @Query("SELECT * FROM task " + "WHERE taskSID = :taskSID")
    abstract fun loadTask(taskSID: String): LiveData<Task>

    @Query("SELECT * FROM task " + "WHERE NOT isUploaded")
    abstract fun loadLocalTasks(): LiveData<List<Task>>
}

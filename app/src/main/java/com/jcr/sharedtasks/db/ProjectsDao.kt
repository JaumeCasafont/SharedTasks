package com.jcr.sharedtasks.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectsReferences(projectReferences: List<ProjectReference>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM ProjectReference")
    fun loadProjectsReferences(): Flow<List<ProjectReference>>

    @Query("SELECT * FROM ProjectReference " + "WHERE projectUUID = :projectUUID")
    fun loadProjectReferenceById(projectUUID: String): Flow<ProjectReference>

    @Query("SELECT * FROM task " + "WHERE taskProjectUUID = :projectUUID ")
    fun loadTasks(projectUUID: String): Flow<List<Task>>

    @Query("SELECT * FROM task " + "WHERE assignee = :assignee AND state < 2")
    fun loadMyTasks(assignee: String): LiveData<List<Task>>

    @Query("SELECT * FROM task " + "WHERE taskSID = :taskSID")
    fun loadTask(taskSID: String): LiveData<Task>

    @Query("SELECT * FROM task " + "WHERE NOT isUploaded")
    fun loadLocalTasks(): LiveData<List<Task>>
}

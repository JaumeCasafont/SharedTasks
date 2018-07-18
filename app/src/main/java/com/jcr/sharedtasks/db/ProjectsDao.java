package com.jcr.sharedtasks.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.model.Task;

import java.util.List;

@Dao
public abstract class ProjectsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertProjectsReferences(List<ProjectReference> projectReferences);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTasks(List<Task> tasks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTask(Task task);

    @Query("SELECT * FROM ProjectReference")
    public abstract LiveData<List<ProjectReference>> loadProjectsReferences();

    @Query("SELECT * FROM ProjectReference "
            + "WHERE projectUUID = :projectUUID")
    public abstract LiveData<ProjectReference> loadProjectReferenceById(String projectUUID);

    @Query("SELECT * FROM task "
            + "WHERE taskProjectUUID = :projectUUID ")
    public abstract LiveData<List<Task>> loadTasks(String projectUUID);

    @Query("SELECT * FROM task "
            + "WHERE assignee = :assignee AND state < 2")
    public abstract LiveData<List<Task>> loadMyTasks(String assignee);

    @Query("SELECT * FROM task "
            + "WHERE taskSID = :taskSID")
    public abstract LiveData<Task> loadTask(String taskSID);

    @Query("SELECT * FROM task "
            + "WHERE NOT isUploaded")
    public abstract LiveData<List<Task>> loadLocalTasks();
}

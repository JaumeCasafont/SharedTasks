package com.jcr.sharedtasks.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.jcr.sharedtasks.model.Project;
import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.repository.ProjectsRepository;

import java.util.List;

@Dao
public abstract class ProjectsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertProject(Project project);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertProjectsReferences(List<ProjectReference> projectReferences);

    @Query("SELECT * FROM Project "
            + "WHERE projectUUID = :projectUUID ")
    public abstract LiveData<Project> loadProject(String projectUUID);

    @Query("SELECT * FROM ProjectReference")
    public abstract LiveData<List<ProjectReference>> loadProjectsReferences();
}

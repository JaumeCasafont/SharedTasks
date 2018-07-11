package com.jcr.sharedtasks.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.jcr.sharedtasks.model.Project;
import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.model.Task;

@Database(entities = {Task.class, Project.class, ProjectReference.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class SharedTasksDb extends RoomDatabase {

    abstract public ProjectsDao tasksDao();
}

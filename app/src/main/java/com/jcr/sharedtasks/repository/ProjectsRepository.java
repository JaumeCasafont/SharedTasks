package com.jcr.sharedtasks.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jcr.sharedtasks.AppExecutors;
import com.jcr.sharedtasks.db.ProjectsDao;
import com.jcr.sharedtasks.db.SharedTasksDb;
import com.jcr.sharedtasks.model.Project;
import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.util.FirebaseQueryLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProjectsRepository {
    private final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
    private final SharedTasksDb db;
    private final ProjectsDao projectsDao;
    private final AppExecutors appExecutors;
    private final SharedPreferences sharedPreferences;

    private List<ProjectReference> projectReferencesCache;

    @Inject
    public ProjectsRepository(AppExecutors appExecutors, SharedTasksDb db, ProjectsDao projectsDao,
                              SharedPreferences sharedPreferences) {
        this.db = db;
        this.projectsDao = projectsDao;
        this.appExecutors = appExecutors;
        this.sharedPreferences = sharedPreferences;

        projectReferencesCache = new ArrayList<>();
    }

    public void createProject(String projectUUID, Project project) {
        appExecutors.diskIO().execute(() -> projectsDao.insertProject(project));
        dataRef.child(projectUUID).push().setValue(project);

        String userUid = sharedPreferences.getString("userUid", "");
        ProjectReference projectReference = new ProjectReference(projectUUID, project.getName());
        dataRef.child(userUid).push().setValue(projectReference);
    }

    public LiveData<List<ProjectReference>> getProjectsReferences() {
        String userUid = sharedPreferences.getString("userUid", "");
        MediatorLiveData<List<ProjectReference>> result = new MediatorLiveData<>();
        LiveData<List<ProjectReference>> dbSource = projectsDao.loadProjectsReferences();
        FirebaseQueryLiveData networkSource = new FirebaseQueryLiveData(dataRef.child(userUid));

        result.addSource(dbSource, result::setValue);

        result.addSource(networkSource, dataSnapshot -> {
            List<ProjectReference> projectReferences = deserializeProjectReference(dataSnapshot);
            if (projectReferences != null) {
                result.setValue(projectReferences);
                appExecutors.diskIO().execute(() -> projectsDao.insertProjectsReferences(projectReferences));
            }
        });

        return result;
    }

    public LiveData<Project> loadProject(String projectUUID) {
        sharedPreferences.edit().putString("lastLoadedProject", projectUUID).apply();
        MediatorLiveData<Project> result = new MediatorLiveData<>();
        LiveData<Project> dbSource = projectsDao.loadProject(projectUUID);
        FirebaseQueryLiveData networkSource = new FirebaseQueryLiveData(dataRef.child(projectUUID));

        result.addSource(dbSource, result::setValue);

        result.addSource(networkSource, dataSnapshot -> {
            Project project = deserializeProject(dataSnapshot);
            if (project != null) {
                result.setValue(project);
                appExecutors.diskIO().execute(() -> projectsDao.insertProject(project));
            }
        });

        return result;
    }

    private List<ProjectReference> deserializeProjectReference(DataSnapshot dataSnapshot) {
        ProjectReference projectReference = dataSnapshot.getValue(ProjectReference.class);
        if (projectReference != null) {
            int position = projectReferencePosition(projectReference);
            if (position == -1) {
                projectReferencesCache.add(projectReference);
            } else {
                projectReferencesCache.set(position, projectReference);
            }
        }

        return projectReferencesCache;
    }

    private int projectReferencePosition(ProjectReference projectReference) {
        if (projectReferencesCache.size() == 0) return -1;
        for (int i = 0; i < projectReferencesCache.size(); i++) {
            if (projectReferencesCache.get(i).getProjectUUID().equals(projectReference.getProjectUUID())) {
                return i;
            }
        }
        return -1;
    }

    private Project deserializeProject(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Project.class);
    }
}

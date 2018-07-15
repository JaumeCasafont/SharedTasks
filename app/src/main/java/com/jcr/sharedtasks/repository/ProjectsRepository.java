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
import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.util.FirebaseChildQueryLiveData;
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
    private String currentProjectUUID;
    private int lastRemotePosition;

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
        dataRef.child(projectUUID).setValue(project);

        String userUid = sharedPreferences.getString("userUid", "");
        ProjectReference projectReference = new ProjectReference(projectUUID, project.getName());
        dataRef.child(userUid).push().setValue(projectReference);
    }

    public LiveData<List<ProjectReference>> getProjectsReferences() {
        String userUid = sharedPreferences.getString("userUid", "");
        MediatorLiveData<List<ProjectReference>> result = new MediatorLiveData<>();
        LiveData<List<ProjectReference>> dbSource = projectsDao.loadProjectsReferences();
        FirebaseChildQueryLiveData networkSource = new FirebaseChildQueryLiveData(dataRef.child(userUid));

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

    public LiveData<ProjectReference> getProjectReferenceById(String projectUUID) {
        return projectsDao.loadProjectReferenceById(projectUUID);
    }

    public LiveData<List<Task>> loadTasks(String projectUUID) {
        currentProjectUUID = projectUUID;
        sharedPreferences.edit().putString("lastLoadedProject", projectUUID).apply();
        MediatorLiveData<List<Task>> result = new MediatorLiveData<>();
        LiveData<List<Task>> dbSource = projectsDao.loadTasks(projectUUID);
        FirebaseQueryLiveData networkSource = new FirebaseQueryLiveData(dataRef.child(projectUUID));

        result.addSource(dbSource, result::setValue);

        result.addSource(networkSource, dataSnapshot -> {
            List<Task> tasks = deserializeProjectTasks(dataSnapshot);
            if (tasks != null) {
                result.setValue(tasks);
                appExecutors.diskIO().execute(() -> projectsDao.insertTasks(tasks));
            }
        });

        return result;
    }

    public LiveData<Task> loadTask(String taskSID) {
        return projectsDao.loadTask(taskSID);
    }

    public void updateTaskStatus(Task task) {
        Task updatedStatusTask = new Task(task);
        updatedStatusTask.setState(updatedStatusTask.getState() + 1);
        if (updatedStatusTask.getState() <= 2) {
            sendTask(updatedStatusTask);
        }
    }

    public void updateTaskAssignee(Task task) {
        Task updatedAssigneeTask = new Task(task);
        updatedAssigneeTask.setAssignee(sharedPreferences.getString("userName", " "));
        sendTask(updatedAssigneeTask);
    }

    public void sendTask(Task task) {
        if (task.getTaskProjectUUID() == null) {
            task.setTaskProjectUUID(currentProjectUUID);
            task.setRemotePosition(lastRemotePosition + 1);
        }
        saveTask(task);
        uploadTask(task);
    }

    private void saveTask(Task task) {
        appExecutors.diskIO().execute(() -> projectsDao.insertTask(task));
    }

    private void uploadTask(Task task) {
        dataRef.child(task.getTaskProjectUUID() + "/tasks/" + String.valueOf(task.getRemotePosition()))
                .setValue(task);
    }

    private List<ProjectReference> deserializeProjectReference(DataSnapshot dataSnapshot) {
        ProjectReference projectReference = dataSnapshot.getValue(ProjectReference.class);
        if (projectReference != null && projectReference.getProjectUUID() != null) {
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

    private List<Task> deserializeProjectTasks(DataSnapshot dataSnapshot) {
        Project project = dataSnapshot.getValue(Project.class);
        addRemotePositions(project);
        return project.getTasks();
    }

    private void addRemotePositions(Project project) {
        for (int i = 0; i < project.getTasks().size(); i++) {
            project.getTasks().get(i).setRemotePosition(i);
            lastRemotePosition = i;
        }
    }
}

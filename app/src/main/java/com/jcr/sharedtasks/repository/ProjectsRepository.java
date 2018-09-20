package com.jcr.sharedtasks.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

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
    private ProjectReference currentReference;
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

    public String getCurrentProjectUUID() {
        if (currentReference == null) return "";
        return currentReference.getProjectUUID();
    }

    public String getCurrentProjectName() {
        if (currentReference == null) return "";
        return currentReference.getProjectName();
    }

    public void createProjectReference(ProjectReference projectReference) {
        String userUid = sharedPreferences.getString("userUid", "");
        if (projectReferencePosition(projectReference) == -1) {
            dataRef.child(userUid).push().setValue(projectReference);
        }
    }

    public void createProject(Project project) {
        dataRef.child(project.getProjectUUID()).setValue(project);

        ProjectReference projectReference = new ProjectReference(project.getProjectUUID(), project.getName());
        createProjectReference(projectReference);
    }

    public LiveData<List<ProjectReference>> getProjectsReferences() {
        String userUid = sharedPreferences.getString("userUid", "");
        MediatorLiveData<List<ProjectReference>> result = new MediatorLiveData<>();
        LiveData<List<ProjectReference>> dbSource = projectsDao.loadProjectsReferences();
        FirebaseChildQueryLiveData networkSource = new FirebaseChildQueryLiveData(dataRef.child(userUid));

        result.addSource(dbSource, result::setValue);

        result.addSource(networkSource, dataSnapshot -> {
            ProjectReference projectReference = deserializeProjectReference(dataSnapshot);
            if (projectReference != null) {
                result.setValue(projectReferencesCache);
                appExecutors.diskIO().execute(() -> projectsDao.insertProjectsReference(projectReference));
            }
        });

        return result;
    }

    public LiveData<ProjectReference> getProjectReferenceById(String projectUUID) {
        MediatorLiveData<ProjectReference> result = new MediatorLiveData<>();
        result.addSource(projectsDao.loadProjectReferenceById(projectUUID), projectReference -> {
            currentReference = projectReference;
            result.setValue(currentReference);
        });
        return result;
    }

    public LiveData<List<Task>> loadTasks(String projectUUID) {
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

    public LiveData<List<Task>> loadMyTasks() {
        return projectsDao.loadMyTasks(
                sharedPreferences.getString("userName", ""));
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
        String assignee = sharedPreferences.getString("userName", " ");
        if (!assignee.equals(updatedAssigneeTask.getAssignee())) {
            updatedAssigneeTask.setAssignee(assignee);
            sendTask(updatedAssigneeTask);
        }
    }

    public void sendTask(Task task) {
        if (task.getTaskProjectUUID() == null) {
            task.setTaskProjectUUID(currentReference.getProjectUUID());
            task.setRemotePosition(lastRemotePosition + 1);
        }
        saveTask(task);
        uploadTask(task);
    }

    public LiveData<List<Task>> loadLocalTasks() {
        return projectsDao.loadLocalTasks();
    }

    private void saveTask(Task task) {
        appExecutors.diskIO().execute(() -> projectsDao.insertTask(task));
    }

    private void uploadTask(Task task) {
        dataRef.child(task.getTaskProjectUUID() + "/tasks/" + String.valueOf(task.getRemotePosition()))
                .setValue(task);
    }

    private ProjectReference deserializeProjectReference(DataSnapshot dataSnapshot) {
        ProjectReference projectReference = dataSnapshot.getValue(ProjectReference.class);
        if (projectReference != null && projectReference.getProjectUUID() != null) {
            int position = projectReferencePosition(projectReference);
            if (position == -1) {
                projectReferencesCache.add(projectReference);
            } else {
                projectReferencesCache.set(position, projectReference);
            }
        }

        return projectReference;
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
        if (project == null) return null;
        syncTasksDataWithServer(project);
        return project.getTasks();
    }

    private void syncTasksDataWithServer(Project project) {
        if (project.getTasks() == null || project.getTasks().isEmpty()) {
            lastRemotePosition = -1;
        } else {
            for (int i = 0; i < project.getTasks().size(); i++) {
                project.getTasks().get(i).setRemotePosition(i);
                project.getTasks().get(i).setUploaded(true);
                lastRemotePosition = i;
            }
        }
    }
}

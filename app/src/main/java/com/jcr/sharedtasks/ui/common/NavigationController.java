package com.jcr.sharedtasks.ui.common;

import androidx.fragment.app.FragmentManager;

import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.ui.MainActivity;
import com.jcr.sharedtasks.ui.createproject.CreateProjectFragment;
import com.jcr.sharedtasks.ui.list.TasksListFragment;
import com.jcr.sharedtasks.ui.taskdetail.TaskDetailFragment;

import javax.inject.Inject;

public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity activity) {
        this.containerId = R.id.container;
        this.fragmentManager = activity.getSupportFragmentManager();
    }

    public void navigateToTasksList(String projectUUID) {
        navigateToTasksList(projectUUID, false);
    }

    public void navigateToTasksList(String projectUUID, boolean popBackStack) {
        if (popBackStack) fragmentManager.popBackStack();
        TasksListFragment tasksListFragment = TasksListFragment.create(projectUUID);
        fragmentManager.beginTransaction()
                .replace(containerId, tasksListFragment)
                .commit();
    }

    public void navigateToTaskDetail(String taskSID) {
        TaskDetailFragment taskDetailFragment = TaskDetailFragment.Companion.create(taskSID);
        String tag = "task" + "/" + taskSID;
        fragmentManager.beginTransaction()
                .replace(containerId, taskDetailFragment, tag)
                .addToBackStack(null)
                .commit();
    }

    public void navigateToCreateProject() {
        CreateProjectFragment createProjectFragment = new CreateProjectFragment();
        String tag = "createProject";
        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragmentManager.beginTransaction()
                    .replace(containerId, createProjectFragment, tag)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

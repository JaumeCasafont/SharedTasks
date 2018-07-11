package com.jcr.sharedtasks.ui.common;

import android.support.v4.app.FragmentManager;

import com.jcr.sharedtasks.ui.MainActivity;
import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.ui.list.TasksListFragment;

import javax.inject.Inject;

public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToTasksList(String projectUUID) {
        TasksListFragment tasksListFragment = TasksListFragment.create(projectUUID);
        fragmentManager.beginTransaction()
                .replace(containerId, tasksListFragment)
                .commit();
    }
}

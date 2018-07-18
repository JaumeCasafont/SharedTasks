package com.jcr.sharedtasks.ui.list;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent;
import com.jcr.sharedtasks.databinding.TasksListFragmentBinding;
import com.jcr.sharedtasks.di.Injectable;
import com.jcr.sharedtasks.ui.common.NavigationController;
import com.jcr.sharedtasks.util.AutoClearedValue;
import com.jcr.sharedtasks.widget.TasksListWidgetService;

import javax.inject.Inject;

import static com.jcr.sharedtasks.BR.projectTasksList;

public class TasksListFragment extends Fragment implements Injectable {

    private static final String PROJECT_UUID_KEY = "projectUUID";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<TasksListFragmentBinding> binding;

    AutoClearedValue<TasksListAdapter> adapter;

    private TasksListViewModel tasksListViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TasksListFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.tasks_list_fragment, container, false,
                        dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tasksListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TasksListViewModel.class);
        TasksListAdapter tasksListAdapter = new TasksListAdapter(dataBindingComponent,
                task -> navigationController.navigateToTaskDetail(task.getTaskSID()),
                tasksListViewModel::updateTaskStatus,
                tasksListViewModel::updateTaskAssignee);

        Bundle args = getArguments();
        if (args.containsKey(PROJECT_UUID_KEY)) {
            tasksListViewModel.setProjectUUID(args.getString(PROJECT_UUID_KEY));
        }

        binding.get().tasksListRv.setAdapter(tasksListAdapter);
        adapter = new AutoClearedValue<>(this, tasksListAdapter);

        fillViews();
        TasksListWidgetService.startActionUpdateIngredientsList(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_people:
                sendInvite();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        hideKeyboard();
    }

    private void hideKeyboard() {
        Activity activity = getActivity();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sendInvite() {
        Intent sendIntent = new Intent();
        String msg = getString(R.string.invite_content);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg + tasksListViewModel.getDeepLinkOfCurrentProject());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void fillViews() {
        tasksListViewModel.getProjectReference().observe(this,
                projectReference -> {
                    if (projectReference != null) {
                        setupActionBar(projectReference.getProjectName());
                    }
                });

        binding.get().addTask.setOnClickListener(v -> onNewTaskClick());
        initRecyclerView();
    }

    private void setupActionBar(String projectName) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(projectName);
    }

    private void initRecyclerView() {
        tasksListViewModel.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                binding.get().setVariable(projectTasksList, tasks);
                adapter.get().replace(tasks);
                binding.get().executePendingBindings();
            }
        });
    }

    public void onNewTaskClick() {
        navigationController.navigateToTaskDetail(null);
    }

    public static TasksListFragment create(String projectUUID) {
        TasksListFragment tasksListFragment = new TasksListFragment();
        Bundle args = new Bundle();
        args.putString(PROJECT_UUID_KEY, projectUUID);
        tasksListFragment.setArguments(args);
        return tasksListFragment;
    }

}

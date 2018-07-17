package com.jcr.sharedtasks.ui.taskdetail;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent;
import com.jcr.sharedtasks.databinding.TaskDetailFragmentBinding;
import com.jcr.sharedtasks.di.Injectable;
import com.jcr.sharedtasks.ui.common.NavigationController;
import com.jcr.sharedtasks.util.AutoClearedValue;
import com.jcr.sharedtasks.util.TimeUtils;

import javax.inject.Inject;

public class TaskDetailFragment extends Fragment implements Injectable, DatePickerDialog.OnDateSetListener {

    private static final String TASK_SID_KEY = "taskSID";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    android.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<TaskDetailFragmentBinding> binding;

    private TaskDetailViewModel taskDetailViewModel;
    private boolean assigneeClicked = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TaskDetailFragmentBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.task_detail_fragment, container, false,
                        dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        taskDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskDetailViewModel.class);

        Bundle args = getArguments();
        if (args.containsKey(TASK_SID_KEY)) {
            taskDetailViewModel.setTaskSID(args.getString(TASK_SID_KEY));
        } else {
            taskDetailViewModel.setTaskSID(null);
        }

        fillViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupActionBar();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_edit_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                if (onSaveClick()) {
                    getActivity().onBackPressed();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (getArguments() != null && getArguments().get(TASK_SID_KEY) != null) {
            actionBar.setTitle(R.string.edit_task);
        } else {
            actionBar.setTitle(R.string.add_task);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void fillViews() {
        taskDetailViewModel.getTask().observe(this, task -> {
            if (task != null) {
                binding.get().setTask(taskDetailViewModel.getTaskToUpload());
            }
        });
        binding.get().priority.setOnClickListener(v -> onPriorityClick());
        binding.get().userName.setOnClickListener(v -> onAssigneeClick());
        binding.get().taskDueDate.setOnClickListener(v -> onDateClick());
    }

    private boolean onSaveClick() {
        String title =  binding.get().taskTitleEt.getText().toString();
        if (!title.isEmpty()) {
            taskDetailViewModel.saveTask(
                    title, binding.get().taskDescriptionEt.getText().toString());
            return true;
        } else {
            Toast.makeText(getContext(), getString(R.string.task_title_empty_error), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void onAssigneeClick() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "com.jcr.sharedtasks", Context.MODE_PRIVATE);
        String nextAssignee = taskDetailViewModel.updateAssignee(
                sharedPreferences.getString("userName", " "));

        binding.get().userName.setText(nextAssignee == null ? getString(R.string.assign_task) : nextAssignee);
    }

    public void onPriorityClick() {
        boolean priority = taskDetailViewModel.updatePriority();
        binding.get().priority.setText(priority ? (getString(R.string.task_high_priority)) :
                (getString(R.string.task_no_priority)));

        Drawable priorityIcon = ContextCompat.getDrawable(getContext(), priority ?
                R.drawable.ic_flag_high_priority : R.drawable.ic_flag_no_priority);
        binding.get().priority.setCompoundDrawablesWithIntrinsicBounds(priorityIcon, null, null, null);
    }

    public void onDateClick() {
        Integer[] date = taskDetailViewModel.getDateToShowInCalendar();
        Dialog dialog = new DatePickerDialog(getContext(), this, date[0], date[1], date[2]);
        dialog.show();
    }

    public static TaskDetailFragment create(String taskSID) {
        TaskDetailFragment taskDetailFragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putString(TASK_SID_KEY, taskSID);
        taskDetailFragment.setArguments(args);
        return taskDetailFragment;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        taskDetailViewModel.updateDate(TimeUtils.getDateInMillis(year, month, dayOfMonth));
        binding.get().taskDueDate.setText(TimeUtils.getDateFormatted(year, month, dayOfMonth));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        taskDetailViewModel.updateTitle(binding.get().taskTitleEt.getText().toString());
        taskDetailViewModel.updateDescription(binding.get().taskDescriptionEt.getText().toString());
    }
}

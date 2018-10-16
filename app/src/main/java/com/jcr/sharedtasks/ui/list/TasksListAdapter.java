package com.jcr.sharedtasks.ui.list;

import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.databinding.TaskItemBinding;
import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.ui.common.DataBoundListAdapter;
import com.jcr.sharedtasks.util.Objects;

public class TasksListAdapter extends DataBoundListAdapter<Task, TaskItemBinding> {
    private final DataBindingComponent dataBindingComponent;
    private final OnTaskClickCallback onTaskClickCallback;
    private final OnTaskClickCallback onCardButtonClick;
    private final OnTaskClickCallback onAssigneeClick;

    public TasksListAdapter(DataBindingComponent dataBindingComponent, OnTaskClickCallback onTaskClickCallback,
                            OnTaskClickCallback onCardButtonClick, OnTaskClickCallback onAssigneeClick) {
        this.dataBindingComponent = dataBindingComponent;
        this.onTaskClickCallback = onTaskClickCallback;
        this.onCardButtonClick = onCardButtonClick;
        this.onAssigneeClick = onAssigneeClick;
    }

    @Override
    protected TaskItemBinding createBinding(ViewGroup parent) {
         TaskItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.task_item,
                parent,
                false,
                dataBindingComponent);

         binding.getRoot().setOnClickListener(v -> executeClick(binding, onTaskClickCallback));

         binding.button.setOnClickListener(v -> executeClick(binding, onCardButtonClick));

         binding.assigneeName.setOnClickListener(v -> executeClick(binding, onAssigneeClick));

        return binding;
    }

    @Override
    protected void bind(TaskItemBinding binding, Task task) {
        binding.setTask(task);
    }

    @Override
    protected boolean areItemsTheSame(Task oldTask, Task newTask) {
        return Objects.equals(oldTask.taskSID, newTask.taskSID);
    }

    @Override
    protected boolean areContentsTheSame(Task oldTask, Task newTask) {
        return Objects.equals(oldTask.taskSID, newTask.taskSID) &&
                Objects.equals(oldTask.assignee, newTask.assignee) &&
                Objects.equals(oldTask.date, newTask.date) &&
                Objects.equals(oldTask.description, newTask.description) &&
                Objects.equals(oldTask.state, newTask.state) &&
                Objects.equals(oldTask.hasPriority, newTask.hasPriority) &&
                Objects.equals(oldTask.title, newTask.title);
    }

    public interface OnTaskClickCallback {
        void onClick(Task task);
    }

    private void executeClick(TaskItemBinding binding, OnTaskClickCallback onTaskClickCallback) {
        Task task = binding.getTask();
        if (task != null && onTaskClickCallback != null) {
            onTaskClickCallback.onClick(task);
        }
    }
}

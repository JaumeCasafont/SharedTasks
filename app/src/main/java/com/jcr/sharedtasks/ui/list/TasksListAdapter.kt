package com.jcr.sharedtasks.ui.list

import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.jcr.sharedtasks.AppExecutors

import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.databinding.TaskItemBinding
import com.jcr.sharedtasks.model.Task
import com.jcr.sharedtasks.ui.common.DataBoundListAdapter
import com.jcr.sharedtasks.util.Objects

class TasksListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val onTaskClickCallback: ((Task) -> Unit)?,
        private val onCardButtonClick: ((Task) -> Unit)?,
        private val onAssigneeClick: ((Task) -> Unit)?
) : DataBoundListAdapter<Task, TaskItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldTask: Task, newTask: Task): Boolean {
                return Objects.equals(oldTask.taskSID, newTask.taskSID)
            }

            override fun areContentsTheSame(oldTask: Task, newTask: Task): Boolean {
                return Objects.equals(oldTask.taskSID, newTask.taskSID) &&
                        Objects.equals(oldTask.assignee, newTask.assignee) &&
                        Objects.equals(oldTask.date, newTask.date) &&
                        Objects.equals(oldTask.description, newTask.description) &&
                        Objects.equals(oldTask.state, newTask.state) &&
                        Objects.equals(oldTask.hasPriority, newTask.hasPriority) &&
                        Objects.equals(oldTask.title, newTask.title)
            }
        }
) {

    override fun createBinding(parent: ViewGroup): TaskItemBinding {
        val binding = DataBindingUtil.inflate<TaskItemBinding>(LayoutInflater.from(parent.context),
                R.layout.task_item,
                parent,
                false,
                dataBindingComponent)

        binding.root.setOnClickListener { executeClick(binding, onTaskClickCallback) }

        binding.button.setOnClickListener { executeClick(binding, onCardButtonClick) }

        binding.assigneeName.setOnClickListener { executeClick(binding, onAssigneeClick) }

        return binding
    }

    override fun bind(binding: TaskItemBinding, task: Task) {
        binding.task = task
    }

    private fun executeClick(binding: TaskItemBinding, onTaskClickCallback: ((Task) -> Unit)?) {
        val task = binding.task
        if (task != null && onTaskClickCallback != null) {
            onTaskClickCallback.invoke(task)
        }
    }
}

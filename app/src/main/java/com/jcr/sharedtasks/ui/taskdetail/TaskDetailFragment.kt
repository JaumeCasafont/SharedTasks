package com.jcr.sharedtasks.ui.taskdetail

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import android.widget.DatePicker
import android.widget.Toast
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent
import com.jcr.sharedtasks.databinding.TaskDetailFragmentBinding
import com.jcr.sharedtasks.di.Injectable
import com.jcr.sharedtasks.util.AutoClearedValue
import com.jcr.sharedtasks.util.TimeUtils
import javax.inject.Inject

class TaskDetailFragment : Fragment(), Injectable, DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    lateinit var binding: AutoClearedValue<TaskDetailFragmentBinding>

    lateinit var taskDetailViewModel: TaskDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
                .inflate<TaskDetailFragmentBinding>(inflater, R.layout.task_detail_fragment, container, false,
                        dataBindingComponent)
        binding = AutoClearedValue(this, dataBinding)

        return binding.get().root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        taskDetailViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskDetailViewModel::class.java)

        val args = arguments
        if (args!!.containsKey("taskSID")) {
            taskDetailViewModel.setTaskSID(args.getString("taskSID"))
        } else {
            taskDetailViewModel.setTaskSID(null)
        }

        fillViews()
    }

    override fun onResume() {
        super.onResume()
        setupActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_add_edit_task, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.save_task -> {
                if (onSaveClick()) {
                    activity!!.onBackPressed()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        if (arguments?.get("taskSID") != null) {
            actionBar.setTitle(R.string.edit_task)
        } else {
            actionBar.setTitle(R.string.add_task)
        }
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    private fun fillViews() {
        taskDetailViewModel.task.observe(this, Observer {
                binding.get().task = taskDetailViewModel.getTaskToUpload()
        })
        binding.get().priority.setOnClickListener { onPriorityClick() }
        binding.get().userName.setOnClickListener { onAssigneeClick() }
        binding.get().taskDueDate.setOnClickListener { onDateClick() }
    }

    private fun onSaveClick(): Boolean {
        val title = binding.get().taskTitleEt.text.toString()
        if (!title.isEmpty()) {
            taskDetailViewModel.saveTask(
                    title, binding.get().taskDescriptionEt.text.toString())
            return true
        } else {
            Toast.makeText(context, getString(R.string.task_title_empty_error), Toast.LENGTH_LONG).show()
            return false
        }
    }

    fun onAssigneeClick() {
        val sharedPreferences = activity!!.getSharedPreferences(
                "com.jcr.sharedtasks", Context.MODE_PRIVATE)
        val nextAssignee = taskDetailViewModel.updateAssignee(
                sharedPreferences.getString("userName", " "))

        binding.get().userName.text = nextAssignee ?: getString(R.string.assign_task)
    }

    fun onPriorityClick() {
        val priority = taskDetailViewModel.updatePriority()
        binding.get().priority.text = if (priority)
            getString(R.string.task_high_priority)
        else
            getString(R.string.task_no_priority)

        val priorityIcon = ContextCompat.getDrawable(context!!, if (priority)
            R.drawable.ic_flag_high_priority
        else
            R.drawable.ic_flag_no_priority)
        binding.get().priority.setCompoundDrawablesWithIntrinsicBounds(priorityIcon, null, null, null)
    }

    fun onDateClick() {
        val date = taskDetailViewModel.dateToShowInCalendar
        val dialog = DatePickerDialog(context!!, this, date[0], date[1], date[2])
        dialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        taskDetailViewModel.updateDate(TimeUtils.getDateInMillis(year, month, dayOfMonth))
        binding.get().taskDueDate.text = TimeUtils.getDateFormatted(year, month, dayOfMonth)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        taskDetailViewModel.updateTitle(binding.get().taskTitleEt.text.toString())
        taskDetailViewModel.updateDescription(binding.get().taskDescriptionEt.text.toString())
    }

    companion object {

        private val TASK_SID_KEY = "taskSID"

        @JvmStatic
        fun create(taskSID: String?): TaskDetailFragment {
            val taskDetailFragment = TaskDetailFragment()
            val args = Bundle()
            args.putString(TASK_SID_KEY, taskSID)
            taskDetailFragment.arguments = args
            return taskDetailFragment
        }
    }
}

package com.jcr.sharedtasks.ui.list

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import com.jcr.sharedtasks.AppExecutors

import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent
import com.jcr.sharedtasks.databinding.TasksListFragmentBinding
import com.jcr.sharedtasks.di.Injectable
import com.jcr.sharedtasks.ui.common.NavigationController
import com.jcr.sharedtasks.util.AutoClearedValue
import com.jcr.sharedtasks.widget.TasksListWidgetService

import javax.inject.Inject

import com.jcr.sharedtasks.BR.projectTasksList

open class TasksListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    lateinit var binding: AutoClearedValue<TasksListFragmentBinding>

    lateinit var adapter: AutoClearedValue<TasksListAdapter>

    lateinit var tasksListViewModel: TasksListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
                .inflate<TasksListFragmentBinding>(inflater, R.layout.tasks_list_fragment, container, false,
                        dataBindingComponent)
        binding = AutoClearedValue(this, dataBinding)

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tasksListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TasksListViewModel::class.java)
        val tasksListAdapter = TasksListAdapter(
                dataBindingComponent,
                appExecutors,
                { task ->
                    navigationController.navigateToTaskDetail(task.getTaskSID())
                },
                { task ->
                    tasksListViewModel.updateTaskStatus(task)
                },
                { task ->
                    tasksListViewModel.updateTaskAssignee(task)
                })

        val args = arguments
        if (args!!.containsKey(PROJECT_UUID_KEY)) {
            tasksListViewModel.setProjectUUID(args.getString(PROJECT_UUID_KEY))
        }

        binding.get().tasksListRv.adapter = tasksListAdapter
        adapter = AutoClearedValue(this, tasksListAdapter)

        fillViews()
        initWidget()
    }

    open fun initWidget() {
        TasksListWidgetService.startActionUpdateIngredientsList(context)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_task_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.add_people -> {
                sendInvite()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        hideKeyboard()
    }

    private fun hideKeyboard() {
        val activity = activity
        val imm = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun sendInvite() {
        val sendIntent = Intent()
        val msg = getString(R.string.invite_content)
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg + tasksListViewModel.deepLinkOfCurrentProject)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun fillViews() {
        tasksListViewModel.projectReference.observe(this, Observer {
            setupActionBar(it?.getProjectName())
        })

        binding.get().addTask.setOnClickListener { v -> onNewTaskClick() }
        initRecyclerView()
    }

    private fun setupActionBar(projectName: String?) {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.title = projectName
    }

    private fun initRecyclerView() {
        tasksListViewModel.tasks.observe(this, Observer { tasks ->
            if (tasks != null) {
                adapter.get().submitList(tasks)
            }
        })
    }

    fun onNewTaskClick() {
        navigationController.navigateToTaskDetail(null)
    }

    companion object {

        private val PROJECT_UUID_KEY = "projectUUID"

        fun create(projectUUID: String): TasksListFragment {
            val tasksListFragment = TasksListFragment()
            val args = Bundle()
            args.putString(PROJECT_UUID_KEY, projectUUID)
            tasksListFragment.arguments = args
            return tasksListFragment
        }
    }

}

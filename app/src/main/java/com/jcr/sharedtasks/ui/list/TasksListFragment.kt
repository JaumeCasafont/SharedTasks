package com.jcr.sharedtasks.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jcr.sharedtasks.AppExecutors
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent
import com.jcr.sharedtasks.databinding.TasksListFragmentBinding
import com.jcr.sharedtasks.di.Injectable
import com.jcr.sharedtasks.ui.common.NavigationController
import com.jcr.sharedtasks.util.autoCleared
import com.jcr.sharedtasks.util.collectOnStarted
import com.jcr.sharedtasks.widget.TasksListWidgetService
import javax.inject.Inject

open class TasksListFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationController: NavigationController

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<TasksListFragmentBinding>()

    var adapter by autoCleared<TasksListAdapter>()

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

        binding = dataBinding

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tasksListViewModel = ViewModelProviders.of(this, viewModelFactory).get(TasksListViewModel::class.java)
        val tasksListAdapter = TasksListAdapter(
                dataBindingComponent,
                appExecutors,
                { task ->
                    navigationController.navigateToTaskDetail(task.taskSID)
                },
                { task ->
                    tasksListViewModel.updateTaskStatus(task)
                },
                { task ->
                    tasksListViewModel.updateTaskAssignee(task)
                })

        val args = arguments
        if (args!!.containsKey(PROJECT_UUID_KEY)) {
            tasksListViewModel.setProjectUUID(args.getString(PROJECT_UUID_KEY)!!)
        }

        binding.tasksListRv.adapter = tasksListAdapter
        adapter = tasksListAdapter

        fillViews()
        initWidget()
    }

    open fun initWidget() {
//        TasksListWidgetService.startActionUpdateIngredientsList(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
        collectOnStarted(tasksListViewModel.projectReference) {
            setupActionBar(it?.projectName)
        }

        binding.addTask.setOnClickListener { v -> onNewTaskClick() }
        initRecyclerView()
    }

    private fun setupActionBar(projectName: String?) {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.title = projectName
    }

    private fun initRecyclerView() {
        collectOnStarted(tasksListViewModel.tasks) { tasks ->
            adapter.submitList(tasks)
        }
    }

    private fun onNewTaskClick() {
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

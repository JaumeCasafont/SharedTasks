package com.jcr.sharedtasks.ui.createproject

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent
import com.jcr.sharedtasks.databinding.FragmentCreateProjectBinding
import com.jcr.sharedtasks.di.Injectable
import com.jcr.sharedtasks.ui.common.NavigationController
import com.jcr.sharedtasks.util.AutoClearedValue

import javax.inject.Inject

class CreateProjectFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationController: NavigationController

    var dataBindingComponent: androidx.databinding.DataBindingComponent = FragmentDataBindingComponent(this)

    lateinit var binding: AutoClearedValue<FragmentCreateProjectBinding>

    lateinit var createProjectViewModel: CreateProjectViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil
                .inflate<FragmentCreateProjectBinding>(inflater, R.layout.fragment_create_project, container, false,
                        dataBindingComponent)
        binding = AutoClearedValue(this, dataBinding)

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        createProjectViewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateProjectViewModel::class.java)

        initViews()
        setupActionBar()
    }

    private fun initViews() {
        binding.get().createProjectBtn.setOnClickListener { v ->
            if (checkInternetConnection()) {
                val projectName = binding.get().projectNameEt.text.toString()
                val newProjectUUID = createProjectViewModel.createProject(projectName)
                if (newProjectUUID != null) {
                    navigationController.navigateToTasksList(newProjectUUID, true)
                } else {
                    Toast.makeText(context, getString(R.string.new_project_error_name), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, getString(R.string.new_project_error_internet), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar ?: return
        actionBar.setTitle(R.string.create_project)
    }

    private fun checkInternetConnection(): Boolean {
        val conMgr = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return (conMgr.activeNetworkInfo != null && conMgr.activeNetworkInfo.isAvailable
                && conMgr.activeNetworkInfo.isConnected)
    }
}

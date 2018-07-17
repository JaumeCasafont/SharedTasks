package com.jcr.sharedtasks.ui.createproject;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.binding.FragmentDataBindingComponent;
import com.jcr.sharedtasks.databinding.FragmentCreateProjectBinding;
import com.jcr.sharedtasks.di.Injectable;
import com.jcr.sharedtasks.ui.common.NavigationController;
import com.jcr.sharedtasks.ui.list.TasksListViewModel;
import com.jcr.sharedtasks.util.AutoClearedValue;

import javax.inject.Inject;

public class CreateProjectFragment extends Fragment  implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    android.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    AutoClearedValue<FragmentCreateProjectBinding> binding;

    private CreateProjectViewModel createProjectViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentCreateProjectBinding dataBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_create_project, container, false,
                        dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createProjectViewModel =  ViewModelProviders.of(this, viewModelFactory).get(CreateProjectViewModel.class);

        initViews();
        setupActionBar();
    }

    private void initViews() {
        binding.get().createProjectBtn.setOnClickListener(v -> {
            String projectName = binding.get().projectNameEt.getText().toString();
            String newProjectUUID = createProjectViewModel.createProject(projectName);
            if (newProjectUUID != null) {
                navigationController.navigateToTasksList(newProjectUUID, true);
            } else {
                Toast.makeText(getContext(), getString(R.string.new_project_error_name), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.create_project);
    }
}

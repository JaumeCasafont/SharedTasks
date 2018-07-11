package com.jcr.sharedtasks.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jcr.sharedtasks.R;
import com.jcr.sharedtasks.model.ProjectReference;
import com.jcr.sharedtasks.ui.common.NavigationController;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    public static final int RC_SIGN_IN = 1;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private MainActivityViewModel mViewModel;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private ArrayAdapter<String> mDrawerAdapter;
    private String currentProjectName;

    private List<ProjectReference> projectReferences = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);

        startFirebaseAuth();
        initDrawer();

        SharedPreferences sharedPreferences = getSharedPreferences("com.jcr.sharedtasks", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("lastLoadedProject") && savedInstanceState == null) {
            navigationController.navigateToTasksList(sharedPreferences.getString("lastLoadedProject", ""));
        }
    }

    private void startFirebaseAuth() {
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                onSignedInInitialize(user);
            } else {
                // User is signed out
                onSignedOutCleanup();
                List<AuthUI.IdpConfig> providers = new ArrayList<>();
                providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    private void initDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.open_drawer_description,
                R.string.close_drawer_description
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(currentProjectName == null ?
                        getString(R.string.app_name) : currentProjectName);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.drawer_opened_title));
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerAdapter = new ArrayAdapter<>(this,
                R.layout.project_reference_item);
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedInInitialize(FirebaseUser user) {
        mViewModel.onSignedInitialize(user);
        mViewModel.getProjectUUIDs().observe(this, result -> {
            projectReferences = result;
            fillDrawer();
        });
    }

    private void fillDrawer() {
        mDrawerAdapter.clear();
        for (ProjectReference reference : projectReferences) {
            mDrawerAdapter.add(reference.projectName);
        }
    }

    private void navigateToFragment(int position) {
        if (projectReferences != null && !projectReferences.isEmpty()) {
            currentProjectName = projectReferences.get(position).getProjectName();
            navigationController.navigateToTasksList(projectReferences.get(position).getProjectUUID());
        }
    }

    private void onSignedOutCleanup() {

    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            navigateToFragment(position);
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }
}

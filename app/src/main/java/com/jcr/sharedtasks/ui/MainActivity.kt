package com.jcr.sharedtasks.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jcr.sharedtasks.R
import com.jcr.sharedtasks.R.id.drawerLayout
import com.jcr.sharedtasks.R.id.leftDrawer
import com.jcr.sharedtasks.model.ProjectReference
import com.jcr.sharedtasks.ui.common.NavigationController
import com.jcr.sharedtasks.util.SyncDataUtil
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationController: NavigationController

    lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    lateinit var mViewModel: MainActivityViewModel

    lateinit var mMenu: Menu
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerAdapter: ArrayAdapter<String>? = null

    private var projectReferences: List<ProjectReference>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel::class.java)

        startFirebaseAuth()
        initDrawer()

        val sharedPreferences = getSharedPreferences("com.jcr.sharedtasks", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("lastLoadedProject") && savedInstanceState == null) {
            val projectUUID = sharedPreferences.getString("lastLoadedProject", "")
            navigationController.navigateToTasksList(projectUUID)
        }
    }

    private fun startFirebaseAuth() {
        mAuthStateListener =
                FirebaseAuth.AuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        // User is signed in
                        onSignedInInitialize(user)
                    } else {
                        // User is signed out
                        onSignedOutCleanup()
                        val providers = ArrayList<AuthUI.IdpConfig>()
                        providers.add(AuthUI.IdpConfig.EmailBuilder().build())

                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN)
                    }
                }
    }

    private fun initDrawer() {
        mDrawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_drawer_description,
                R.string.close_drawer_description
        )

        drawerLayout.addDrawerListener(mDrawerToggle!!)
        mDrawerAdapter = ArrayAdapter(this,
                R.layout.project_reference_item)
        leftDrawer.adapter = mDrawerAdapter
        leftDrawer.onItemClickListener = DrawerItemClickListener()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main_activity, mMenu)
        if (mViewModel.isUserLogged) {
            mMenu.findItem(R.id.create_project).isEnabled = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.create_project -> navigationController.navigateToCreateProject()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }

    private fun onSignedInInitialize(user: FirebaseUser) {
        mViewModel.onSignedInitialize(user)
        mViewModel.projectUUIDs.observe(this, Observer {
            projectReferences = it
            fillDrawer()
        })
        if (intent.data != null) {
            navigationController.navigateToTasksList(
                    mViewModel.parseDeeplink(intent.data))
            intent.data = null
        }
        SyncDataUtil.startSyncData(this)
    }

    private fun fillDrawer() {
        mDrawerAdapter!!.clear()
        for (reference in projectReferences!!) {
            mDrawerAdapter!!.add(reference.projectName)
        }
    }

    private fun navigateToFragment(position: Int) {
        if (projectReferences != null && !projectReferences!!.isEmpty()) {
            val projectUUID = projectReferences!![position].getProjectUUID()
            navigationController.navigateToTasksList(projectUUID)
        }
    }

    private fun onSignedOutCleanup() {

    }

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment>? {
        return dispatchingAndroidInjector
    }

    private inner class DrawerItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            navigateToFragment(position)
            drawerLayout.closeDrawer(Gravity.START)
        }
    }

    companion object {
        val RC_SIGN_IN = 1
    }
}

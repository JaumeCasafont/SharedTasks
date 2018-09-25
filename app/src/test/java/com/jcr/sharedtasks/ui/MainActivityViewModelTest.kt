package com.jcr.sharedtasks.ui

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseUser
import com.jcr.sharedtasks.repository.ProjectsRepository
import com.jcr.sharedtasks.repository.SignInRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class MainActivityViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()
    private val signInRepository = Mockito.mock(SignInRepository::class.java)
    private val projectsRepository = Mockito.mock(ProjectsRepository::class.java)
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun init() {
        viewModel = MainActivityViewModel(signInRepository, projectsRepository);
    }

    @Test
    fun whenSignInRepositoryIsCalledAndLoggedSetToTrue() {
        val userMock = mock(FirebaseUser::class.java)
        viewModel.onSignedInitialize(userMock)
        verify(signInRepository).onSignedInitialize(userMock)
        assert(viewModel.isUserLogged)
    }
}
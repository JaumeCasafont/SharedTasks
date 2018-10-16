package com.jcr.sharedtasks.ui.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class TasksListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private ProjectsRepository repository;
    private TasksListViewModel tasksListViewModel;

    @Before
    public void setup() {
        repository = mock(ProjectsRepository.class);
        tasksListViewModel = new TasksListViewModel(repository);
    }

    @Test
    public void givenAProjectUUIDThenViewModelLoadsTasks() {
        Observer<List<Task>> tasksResult = mock(Observer.class);
        tasksListViewModel.getTasks().observeForever(tasksResult);
        tasksListViewModel.setProjectUUID("projectUUID");
        verify(repository).loadTasks("projectUUID");
    }

    @Test
    public void givenTheSameProjectUUIDThenNoCallsAreMade() {
        tasksListViewModel.getTasks().observeForever(mock(Observer.class));
        tasksListViewModel.setProjectUUID("projectUUID");
        verify(repository).loadTasks("projectUUID");
        tasksListViewModel.setProjectUUID("projectUUID");
        verifyNoMoreInteractions(repository);
    }
}

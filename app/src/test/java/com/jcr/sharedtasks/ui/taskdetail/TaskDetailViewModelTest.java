package com.jcr.sharedtasks.ui.taskdetail;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.jcr.sharedtasks.model.Task;
import com.jcr.sharedtasks.repository.ProjectsRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class TaskDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private ProjectsRepository repository;
    private TaskDetailViewModel taskDetailViewModel;

    @Before
    public void setup() {
        repository = mock(ProjectsRepository.class);
        taskDetailViewModel = new TaskDetailViewModel(repository);
    }

    @Test
    public void givenATaskSIDThenLoadsIt() {
        Observer<Task> taskResult = mock(Observer.class);
        taskDetailViewModel.getTask().observeForever(taskResult);
        taskDetailViewModel.setTaskSID("taskSID");
        verify(repository).loadTask("taskSID");
    }

    @Test
    public void givenTheSameTaskSIDThenNoCallsAreMade() {
        Observer<Task> taskResult = mock(Observer.class);
        taskDetailViewModel.getTask().observeForever(taskResult);
        taskDetailViewModel.setTaskSID("taskSID");
        verify(repository).loadTask("taskSID");
        taskDetailViewModel.setTaskSID("taskSID");
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void givenATitleAndDescriptionViewModelCreatesTasksAndSendItToRepository() {
        taskDetailViewModel.setTaskSID(null);
        taskDetailViewModel.saveTask("title", "description");
        verify(repository).sendTask(taskDetailViewModel.getTaskToUpload());
    }

    @Test
    public void applyChangesToTaskThenTaskToUploadIsCorrect() {
        taskDetailViewModel.setTaskSID(null);
        taskDetailViewModel.updateTitle("title");
        taskDetailViewModel.updateDescription("description");
        taskDetailViewModel.updatePriority();
        taskDetailViewModel.updateDate(1000L);

        Task taskToUpload = taskDetailViewModel.getTaskToUpload();
        assertThat(taskToUpload.getTitle(), is("title"));
        assertThat(taskToUpload.getDescription(), is("description"));
        assertThat(taskToUpload.hasPriority(), is(true));
        assertThat(taskToUpload.getDate(), is(1000L));
    }
}

package com.jcr.sharedtasks.ui.createproject

import androidx.lifecycle.ViewModel

import com.jcr.sharedtasks.model.Project
import com.jcr.sharedtasks.repository.ProjectsRepository

import java.util.UUID

import javax.inject.Inject

class CreateProjectViewModel @Inject
constructor(private val repository: ProjectsRepository) : ViewModel() {

    fun createProject(projectName: String): String? {
        if (projectName.isEmpty()) return null
        val projectUUID = UUID.randomUUID().toString()
        val project = Project(projectUUID, projectName)
        repository.createProject(project)
        return projectUUID
    }
}

package com.jcr.sharedtasks.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        indices = [
            Index("taskProjectUUID")
        ],
        primaryKeys = ["taskSID", "taskProjectUUID"]
)
data class Task(
        var taskProjectUUID: String,
        var taskSID: String,
        var date: Long = 0,
        var title: String,
        var assignee: String?,
        var description: String?,
        var state: Int = 0,
        var hasPriority: Boolean = false,
        var remotePosition: Int = 0,
        var isUploaded: Boolean = false
) {
    @Ignore
    constructor(): this(
            "",
            "",
            0L,
            "",
            null,
            "",
            0,
            false,
            0,
            false)

    @Ignore
    constructor(taskProjectUUID: String, taskSID: String) : this(
            taskProjectUUID,
            taskSID,
            0L,
            "",
            null,
            "",
            0,
            false,
            0,
            false)

    @Ignore
    constructor(task: Task) : this(
            task.taskProjectUUID,
            task.taskSID,
            task.date,
            task.title,
            task.assignee,
            task.description,
            task.state,
            task.hasPriority,
            task.remotePosition,
            task.isUploaded
    )

    @Ignore
    constructor(taskProjectUUID: String, taskSID: String, title: String) : this(
            taskProjectUUID,
            taskSID,
            0L,
            title,
            null,
            "",
            0,
            false,
            0,
            false)
}

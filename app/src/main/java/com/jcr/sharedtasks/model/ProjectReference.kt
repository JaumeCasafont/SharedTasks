package com.jcr.sharedtasks.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        indices = [
            Index("projectUUID")
        ],
        primaryKeys = ["projectUUID"]
)
data class ProjectReference(
    var projectUUID: String,
    var projectName: String
) {
    @Ignore
    constructor(): this("", "")
}

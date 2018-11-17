package com.jcr.sharedtasks.model

data class Project(
        var projectUUID: String,
        var tasks: List<Task>? = null,
        var name: String
) {

    constructor(projectUUID: String, name: String) : this(
            projectUUID,
            null,
            name
    )

    constructor() : this(
            "",
            ""
    )
}

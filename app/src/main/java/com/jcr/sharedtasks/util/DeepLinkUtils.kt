package com.jcr.sharedtasks.util

import android.net.Uri

import com.jcr.sharedtasks.model.ProjectReference

object DeepLinkUtils {

    val DEEPLINK_HOST = "https://com.jcr.sharedtasks/"

    fun parseProjectUUID(data: Uri): ProjectReference {
        val deepLinkPath = data.encodedPath?.toString()
        val splitDeepLink = deepLinkPath?.split("/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        val projectName = splitDeepLink?.get(1)
        val projectUUID = splitDeepLink?.get(2)
        return ProjectReference(projectUUID, projectName?.replace("&", " "))
    }
}

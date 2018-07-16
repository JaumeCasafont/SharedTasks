package com.jcr.sharedtasks.util;

import android.net.Uri;

import com.jcr.sharedtasks.model.ProjectReference;

public final class DeepLinkUtils {

    public static final String DEEPLINK_HOST = "https://com.jcr.sharedtasks/";

    public static ProjectReference parseProjectUUID(Uri data) {
        String deepLinkPath = data.getEncodedPath().toString();
        String[] splitDeepLink = deepLinkPath.split("/");
        String projectName = splitDeepLink[1];
        String projectUUID = splitDeepLink[2];
        return new ProjectReference(projectUUID, projectName.replace("&", " "));
    }
}

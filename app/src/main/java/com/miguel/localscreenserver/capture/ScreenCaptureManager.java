package com.miguel.localscreenserver.capture;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

public class ScreenCaptureManager {

    public static final int REQUEST_CODE = 1001;

    private final MediaProjectionManager mediaProjectionManager;

    public ScreenCaptureManager(Activity activity) {
        mediaProjectionManager =
                (MediaProjectionManager) activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE);
    }

    public void requestCapture(Activity activity) {
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public MediaProjection getProjection(int resultCode, Intent data) {
        return mediaProjectionManager.getMediaProjection(resultCode, data);
    }
}
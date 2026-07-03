package com.miguel.localscreenserver.capture;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenCaptureEngine {

    private final Context context;
    private final MediaProjection projection;

    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;

    public ScreenCaptureEngine(Context context,
                               MediaProjection projection) {

        this.context = context;
        this.projection = projection;

    }

}
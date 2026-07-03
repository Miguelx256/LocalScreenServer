package com.miguel.localscreenserver.capture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.nio.ByteBuffer;

public class ScreenCaptureSession {

    private final Context context;
    private final MediaProjection mediaProjection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private Bitmap reusableBitmap;
    private int captureWidth;
    private int captureHeight;

    public ScreenCaptureSession(Context context, MediaProjection projection) {
        this.context = context;
        this.mediaProjection = projection;
    }

    public void start() {

        WindowManager wm =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);

        int width = (int)(metrics.widthPixels * 0.66f);
        int height = (int)(metrics.heightPixels * 0.66f);

        imageReader = ImageReader.newInstance(
                width,
                height,
                PixelFormat.RGBA_8888,
                3);

        captureWidth = width;
        captureHeight = height;

        reusableBitmap = Bitmap.createBitmap(
                captureWidth,
                captureHeight,
                Bitmap.Config.ARGB_8888
        );

        virtualDisplay =
                mediaProjection.createVirtualDisplay(
                        "LocalScreenServer",
                        width,
                        height,
                        metrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(),
                        null,
                        null);
    }

    public Bitmap captureBitmap() {

        Image image = imageReader.acquireLatestImage();

        if (image == null)
            return null;

        Image.Plane plane = image.getPlanes()[0];

        ByteBuffer buffer = plane.getBuffer();

        reusableBitmap.copyPixelsFromBuffer(buffer);

        image.close();

        return reusableBitmap;
    }

    public void stop() {

        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }

        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }

    }

}
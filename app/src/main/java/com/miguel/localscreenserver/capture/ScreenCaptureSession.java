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

        ImageReader.newInstance(
                width,
                height,
                PixelFormat.RGBA_8888,
                3);

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

        int pixelStride = plane.getPixelStride();
        int rowStride = plane.getRowStride();

        int rowPadding = rowStride - pixelStride * image.getWidth();

        int width = image.getWidth();
        int height = image.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
        );

        bitmap.copyPixelsFromBuffer(buffer);

        image.close();

        return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                width,
                height
        );
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
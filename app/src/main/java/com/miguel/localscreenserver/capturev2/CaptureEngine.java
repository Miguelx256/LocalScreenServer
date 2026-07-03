package com.miguel.localscreenserver.capturev2;

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

import com.miguel.localscreenserver.capturev2.frame.Frame;

import java.nio.ByteBuffer;

public class CaptureEngine {

    private final Context context;
    private final MediaProjection projection;
    private final CaptureConfig config;

    private final LatestFrame latestFrame;
    private final BitmapPool bitmapPool;
    private final FrameQueue frameQueue;

    private EncoderThread encoderThread;

    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;

    private long frameCounter = 0;

    public CaptureEngine(Context context,
                         MediaProjection projection,
                         CaptureConfig config) {

        this.context = context;
        this.projection = projection;
        this.config = config;

        latestFrame = new LatestFrame();
        bitmapPool = new BitmapPool();
        frameQueue = new FrameQueue();
    }

    public void start() {

        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager wm =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getRealMetrics(metrics);

        int width =
                (int) (metrics.widthPixels * config.scale);

        int height =
                (int) (metrics.heightPixels * config.scale);

        imageReader =
                ImageReader.newInstance(
                        width,
                        height,
                        PixelFormat.RGBA_8888,
                        3);

        virtualDisplay =
                projection.createVirtualDisplay(
                        "CaptureEngine",
                        width,
                        height,
                        metrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        imageReader.getSurface(),
                        null,
                        null);

        encoderThread =
                new EncoderThread(
                        frameQueue,
                        latestFrame);

        encoderThread.start();

        imageReader.setOnImageAvailableListener(reader -> {

            Image image = reader.acquireLatestImage();

            if (image == null)
                return;

            try {

                Bitmap bitmap =
                        bitmapPool.get(
                                image.getWidth(),
                                image.getHeight());

                Image.Plane plane =
                        image.getPlanes()[0];

                ByteBuffer buffer =
                        plane.getBuffer();

                buffer.rewind();

                bitmap.copyPixelsFromBuffer(buffer);

                Frame frame = new Frame();

                frame.bitmap = bitmap;
                frame.width = image.getWidth();
                frame.height = image.getHeight();
                frame.timestamp = System.currentTimeMillis();
                frame.frameNumber = ++frameCounter;

                frameQueue.offer(frame);

            } finally {

                image.close();

            }

        }, null);

    }

    public void stop() {

        if (encoderThread != null) {

            encoderThread.shutdown();
            encoderThread = null;

        }

        if (virtualDisplay != null) {

            virtualDisplay.release();
            virtualDisplay = null;

        }

        if (imageReader != null) {

            imageReader.close();
            imageReader = null;

        }

        bitmapPool.clear();

        latestFrame.clear();

    }

    public LatestFrame getLatestFrame() {

        return latestFrame;

    }

}
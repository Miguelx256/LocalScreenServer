package com.miguel.localscreenserver.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.miguel.localscreenserver.R;
import com.miguel.localscreenserver.capture.ImageEncoder;
import com.miguel.localscreenserver.capture.ScreenCaptureSession;
import com.miguel.localscreenserver.model.FrameBuffer;

public class ScreenCaptureService extends Service {

    public static final String CHANNEL_ID = "screen_capture_channel";
    public static final int NOTIFICATION_ID = 100;

    public static boolean running = false;

    private Handler handler;
    private Runnable captureLoop;

    private MediaProjection projection;
    private ScreenCaptureSession session;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Local Screen Server")
                        .setContentText("Capturando pantalla...")
                        .setOngoing(true)
                        .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("LocalScreenServer", "onStartCommand()");

        if (running) {
            return START_STICKY;
        }

        if (intent == null) {
            return START_NOT_STICKY;
        }

        int resultCode =
                intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);

        Intent data =
                intent.getParcelableExtra("data");

        if (resultCode != Activity.RESULT_OK || data == null) {

            Log.d("LocalScreenServer", "Datos inválidos");

            stopSelf();

            return START_NOT_STICKY;
        }

        MediaProjectionManager manager =
                (MediaProjectionManager)
                        getSystemService(MEDIA_PROJECTION_SERVICE);

        projection =
                manager.getMediaProjection(resultCode, data);

        session =
                new ScreenCaptureSession(this, projection);

        session.start();

        running = true;

        Log.d("LocalScreenServer", "Captura iniciada");

        handler = new Handler(getMainLooper());

        captureLoop = new Runnable() {

            @Override
            public void run() {

                if (!running) {
                    return;
                }

                Bitmap bitmap = session.captureBitmap();

                if (bitmap != null) {

                    byte[] jpeg =
                            ImageEncoder.toJpeg(bitmap);

                    FrameBuffer.setFrame(jpeg);

                }

                // 50 ms = 20 FPS
                handler.postDelayed(this, 30);

            }

        };

        handler.postDelayed(captureLoop, 1000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        running = false;

        if (handler != null && captureLoop != null) {
            handler.removeCallbacks(captureLoop);
        }

        if (session != null) {
            try {
                session.stop();
            } catch (Exception ignored) {
            }
        }

        if (projection != null) {
            try {
                projection.stop();
            } catch (Exception ignored) {
            }
        }

        FrameBuffer.clear();

        stopForeground(STOP_FOREGROUND_REMOVE);

        Log.d("LocalScreenServer", "Servicio detenido");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Captura de pantalla",
                            NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);

        }

    }

}
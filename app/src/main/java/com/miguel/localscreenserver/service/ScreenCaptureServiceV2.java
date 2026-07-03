package com.miguel.localscreenserver.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.miguel.localscreenserver.R;
import com.miguel.localscreenserver.capturev2.CaptureConfig;
import com.miguel.localscreenserver.capturev2.CaptureEngine;

public class ScreenCaptureServiceV2 extends Service {

    public static final String CHANNEL_ID = "capture_v2";

    public static CaptureEngine engine;

    @Override
    public void onCreate() {
        super.onCreate();

        createChannel();

        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("LocalScreenServer V2")
                        .setContentText("Capturando pantalla...")
                        .setOngoing(true)
                        .build();

        startForeground(100, notification);
    }

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {

        if (intent == null)
            return START_NOT_STICKY;

        int resultCode =
                intent.getIntExtra(
                        "resultCode",
                        Activity.RESULT_CANCELED);

        Intent data =
                intent.getParcelableExtra("data");

        if (resultCode != Activity.RESULT_OK || data == null)
            return START_NOT_STICKY;

        MediaProjectionManager manager =
                (MediaProjectionManager)
                        getSystemService(MEDIA_PROJECTION_SERVICE);

        MediaProjection projection =
                manager.getMediaProjection(
                        resultCode,
                        data);

        CaptureConfig config = new CaptureConfig();

        config.scale = 0.75f;
        config.jpegQuality = 85;
        config.fps = 20;

        engine = new CaptureEngine(
                this,
                projection,
                config);

        engine.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (engine != null) {

            engine.stop();

            engine = null;

        }

        stopForeground(STOP_FOREGROUND_REMOVE);

        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Capture",
                            NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);

        }

    }

}
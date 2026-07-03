package com.miguel.localscreenserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.miguel.localscreenserver.capture.ScreenCaptureManager;
import com.miguel.localscreenserver.serverv2.WebServerV2;
import com.miguel.localscreenserver.service.ScreenCaptureServiceV2;
import com.miguel.localscreenserver.utils.NetworkUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView txtStatus;
    private TextView txtIP;
    private TextView txtURL;

    private Button btnStart;

    private WebServerV2 server;
    private ScreenCaptureManager captureManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        txtIP = findViewById(R.id.txtIP);
        txtURL = findViewById(R.id.txtURL);
        btnStart = findViewById(R.id.btnStart);

        captureManager = new ScreenCaptureManager(this);

        String ip = NetworkUtils.getIPAddress(this);

        txtIP.setText("IP: " + ip);
        txtURL.setText("http://" + ip + ":8080");

        btnStart.setText("Iniciar servidor");

        btnStart.setOnClickListener(v -> {

            if (server == null) {

                captureManager.requestCapture(this);

            } else {

                detenerServidor();

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ScreenCaptureManager.REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK && data != null) {

                Intent serviceIntent =
                        new Intent(this, ScreenCaptureServiceV2.class);

                serviceIntent.putExtra("resultCode", resultCode);
                serviceIntent.putExtra("data", data);

                // 1. Primero inicia el servicio
                startForegroundService(serviceIntent);

                // 2. Espera a que el servicio cree CaptureEngine
                new android.os.Handler(getMainLooper()).postDelayed(() -> {

                    try {

                        if (server == null) {

                            server = new WebServerV2(
                                    ScreenCaptureServiceV2.engine.getLatestFrame());

                            server.start();

                        }

                        txtStatus.setText("Servidor iniciado");
                        btnStart.setText("Detener servidor");

                    } catch (IOException e) {

                        txtStatus.setText("Error iniciando servidor");
                        e.printStackTrace();

                    }

                }, 500);

            } else {

                txtStatus.setText("Permiso denegado");

            }

        }

    }

    private void detenerServidor() {

        if (server != null) {

            server.stop();
            server = null;

        }

        stopService(new Intent(this, ScreenCaptureServiceV2.class));

        txtStatus.setText("Servidor detenido");
        btnStart.setText("Iniciar servidor");

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (isFinishing()) {

            detenerServidor();

        }

    }

}
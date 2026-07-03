package com.miguel.localscreenserver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.miguel.localscreenserver.capture.ScreenCaptureManager;
import com.miguel.localscreenserver.server.WebServer;
import com.miguel.localscreenserver.service.ScreenCaptureService;
import com.miguel.localscreenserver.utils.NetworkUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView txtStatus;
    private TextView txtIP;
    private TextView txtURL;

    private Button btnStart;

    private WebServer server;
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

            if (!ScreenCaptureService.running) {

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

                try {

                    if (server == null) {

                        server = new WebServer();
                        server.start();

                    }

                } catch (IOException e) {

                    txtStatus.setText("Error iniciando servidor");

                    return;

                }

                Intent serviceIntent =
                        new Intent(this, ScreenCaptureService.class);

                serviceIntent.putExtra("resultCode", resultCode);
                serviceIntent.putExtra("data", data);

                startForegroundService(serviceIntent);

                txtStatus.setText("Servidor iniciado");
                btnStart.setText("Detener servidor");

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

        stopService(new Intent(this, ScreenCaptureService.class));

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
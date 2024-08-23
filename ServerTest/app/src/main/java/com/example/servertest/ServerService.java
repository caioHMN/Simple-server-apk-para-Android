package com.example.servertest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class ServerService extends Service {

    private WebServer webServer;
    private CameraManager cameraManager;
    private String cameraId;
    private static final String TAG = "ServerService";

    @Override
    public void onCreate() {
        super.onCreate();
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera access error: ", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int port = intent.getIntExtra("PORT", 8080);
        webServer = new WebServer(port);

        try {
            webServer.start();
            Log.d(TAG, "Server started on port: " + port);
        } catch (IOException e) {
            Log.e(TAG, "Error starting server: ", e);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webServer != null) {
            webServer.stop();
            Log.d(TAG, "Server stopped");
        }
        turnOffFlashlight();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class WebServer extends NanoHTTPD {

        public WebServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String response;
            Map<String, String> params = session.getParms();

            if ("/flash_on".equalsIgnoreCase(session.getUri())) {
                turnOnFlashlight();
                response = "Lanterna Ligada";
            } else if ("/flash_off".equalsIgnoreCase(session.getUri())) {
                turnOffFlashlight();
                response = "Lanterna Desligada";
            } else {
                response = "<html><body>" +
                        "<h1>Controle da Lanterna</h1>" +
                        "<button onmousedown=\"fetch('/flash_on')\" onmouseup=\"fetch('/flash_off')\">Pulse</button>" +
                        "</body></html>";
            }

            return newFixedLengthResponse(response);
        }
    }

    private void turnOnFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Cannot turn on flashlight", e);
        }
    }

    private void turnOffFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Cannot turn off flashlight", e);
        }
    }
}

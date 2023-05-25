package com.looigi.gpsone.AutoStart;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.looigi.gpsone.Log;

public class RunServiceOnBoot extends android.app.Service {
    private static String TAG = "WallpapaerChanger";
    private Handler handler;
    private Runnable runnable;
    private final int runTime = 5000;
    @Override
    public void onCreate() {
        super.onCreate();

        // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Log.getInstance().ScriveLog("On Create runServiceOnBoot");
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, runTime);
            }
        };
        handler.post(runnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.getInstance().ScriveLog("On Start runServiceOnBoot");
    }
}
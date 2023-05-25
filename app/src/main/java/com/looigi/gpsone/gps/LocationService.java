/* package com.looigi.gpsone.gps;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.MainActivity;
import com.looigi.gpsone.R;
import com.looigi.gpsone.Utility;
import com.looigi.gpsone.VariabiliGlobali;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        startMyOwnForeground();
    }

    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.looigi.gpsone";
        String channelName = "GpsOne";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        // Utility.getInstance().InstanziaNotifica();
        // if (VariabiliGlobali.getInstance().getNotification() != null) {
            startForeground(1, notification); // VariabiliGlobali.getInstance().getNotification().build());
        // }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        //do something you want
        //stop service
        stopForeground(true);
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLocationListener m = new MyLocationListener();
        m.AzionaServizio();

        VariabiliGlobali.getInstance().setMascheraAperta(false);
        Activity a = MainActivity.getAppActivity();
        if (a != null) {
            a.moveTaskToBack(true);
        }
        VariabiliGlobali.getInstance().setePartito(true);

        // return super.onStartCommand(intent, flags, startId);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        VariabiliGlobali.getInstance().setServizioGPS(false);
        // stopForeground(true);
        // stopSelf();
    }
}
*/
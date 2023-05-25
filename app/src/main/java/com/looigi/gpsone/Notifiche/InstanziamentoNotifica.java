package com.looigi.gpsone.Notifiche;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.appsearch.GetSchemaResponse;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.MainActivity;
import com.looigi.gpsone.R;
import com.looigi.gpsone.VariabiliGlobali;
import com.looigi.gpsone.gps.MyLocationListener;

public class InstanziamentoNotifica extends Service {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.getInstance().ScriveLog("Instanziamento notifica: On create" );

        GestioneNotifiche.getInstance().setCtx(this);
        GestioneNotifiche.getInstance().setTitolo("");
        GestioneNotifiche.getInstance().setTitolo2("");

        Notification notification = GestioneNotifiche.getInstance().StartApplicazione();

        startForeground(GestioneNotifiche.getInstance().getIdNotifica(), notification);
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
        Log.getInstance().ScriveLog("Instanziamento notifica: On start command" );

        MyLocationListener m = new MyLocationListener();
        m.AzionaServizio();

        VariabiliGlobali.getInstance().setMascheraAperta(false);
        Activity a = MainActivity.getAppActivity();
        if (a != null) {
            a.moveTaskToBack(true);
        }
        VariabiliGlobali.getInstance().setePartito(true);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
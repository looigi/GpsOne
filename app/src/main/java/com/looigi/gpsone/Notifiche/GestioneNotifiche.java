package com.looigi.gpsone.Notifiche;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.R;
import com.looigi.gpsone.Utility;
import com.looigi.gpsone.VariabiliGlobali;

public class GestioneNotifiche {
    private NotificationManager manager;
    private NotificationCompat.Builder notificationBuilder;
    private RemoteViews contentView;
    private final int idNotifica = 456789;
    private String channelName = "gpsone";
    private String NOTIFICATION_CHANNEL_ID = "com.looigi.gpsone";

    private static final GestioneNotifiche ourInstance = new GestioneNotifiche();

    public static GestioneNotifiche getInstance() {
        return ourInstance;
    }

    private GestioneNotifiche() {
    }

    public int getIdNotifica() {
        return idNotifica;
    }

    private String Titolo;
    private String Titolo2;
    private Context ctx;

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public void setTitolo2(String titolo2) {
        Titolo2 = titolo2;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public Notification StartApplicazione() {
        if (ctx == null) {
            return null;
        } else {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            chan.setSound(null, null);
            chan.setImportance(NotificationManager.IMPORTANCE_LOW);

            manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            Intent intent = new Intent(ctx, PassaggioNotifica.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, idNotifica, intent, 0);

            contentView = new RemoteViews(ctx.getPackageName(), R.layout.barra_notifica);
            setListenersTasti(contentView);
            setListeners(contentView);

            if (Titolo != null && !Titolo.isEmpty()) {
                contentView.setTextViewText(R.id.txtTitoloNotifica, Titolo);
            } else {
                contentView.setTextViewText(R.id.txtTitoloNotifica, "---");
            }
            if (Titolo2 != null && !Titolo2.isEmpty()) {
                contentView.setTextViewText(R.id.txtTitoloNotifica2, Titolo2);
            } else {
                contentView.setTextViewText(R.id.txtTitoloNotifica2, "---");
            }

            notificationBuilder = new NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID);
            return notificationBuilder
                    .setContentTitle("GpsOne")                            // required
                    .setSmallIcon(R.drawable.logo)   // required android.R.drawable.ic_menu_slideshow
                    .setContentText("GpsOne") // required
                    // .setDefaults(Notification.DEFAULT_ALL)
                    .setOnlyAlertOnce(false)
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    // .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                    // .setGroup("LOO'S WEB PLAYER")
                    // .setGroupSummary(true)
                    // .setDefaults(NotificationCompat.DEFAULT_ALL)
                    // .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setContentIntent(pendingIntent)
                    .setTicker("")
                    .setContent(contentView)
                    .build();

            // GestioneNotifiche.getInstance().ImpostaValori(manager, idNotifica, notificationBuilder, contentView);
        }
    }

    public void AggiornaNotifica() {
        if (ctx != null) {
            Notification notification = StartApplicazione();
            NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(idNotifica, notification);
        }
    }

    public void RimuoviNotifica() {
        Log.getInstance().ScriveLog("Rimuovi notifica");
        if (manager != null) {
            try {
                manager.cancel(idNotifica);
                manager = null;
                notificationBuilder = null;
                // NOTIF_ID++;
            } catch (Exception e) {
                Log.getInstance().ScriveLog(Utility.getInstance().PrendeErroreDaException(e));
            }
        }
    }

    private void setListeners(RemoteViews view) {
        if (view != null) {
            Log.getInstance().ScriveLog("Set Listeners. View corretta");
        } else {
            Log.getInstance().ScriveLog("Set Listeners. View NON corretta");
            return;
        }

        view.setTextViewText(R.id.txtTitoloNotifica, "");
        view.setTextViewText(R.id.txtTitoloNotifica2, "");
        /* if (CeSegnale) {
            view.setViewVisibility(R.id.imgCeSegnale, LinearLayout.VISIBLE);
        } else {
            view.setViewVisibility(R.id.imgCeSegnale, LinearLayout.GONE);
        } */

        if (VariabiliGlobali.getInstance().isServizioGPS()) {
            view.setImageViewResource(R.id.imgPlayStop, R.drawable.icona_stop);
        } else {
            view.setImageViewResource(R.id.imgPlayStop, R.drawable.icona_play);
        }
    }

    private void setListenersTasti(RemoteViews view) {
        if (view != null) {
            Log.getInstance().ScriveLog("Set Listeners tasti. View corretta" );

            Intent apre = new Intent(ctx, PassaggioNotifica.class);
            apre.putExtra("DO", "apre");
            PendingIntent pApre = PendingIntent.getActivity(ctx, 0, apre, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.layBarraNotifica, pApre);

            Intent playStop = new Intent(ctx, PassaggioNotifica.class);
            playStop.putExtra("DO", "playStop");
            PendingIntent pPlayStop= PendingIntent.getActivity(ctx, 1, playStop, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.imgPlayStop, pPlayStop);

            Intent cambioSezione = new Intent(ctx, PassaggioNotifica.class);
            cambioSezione.putExtra("DO", "cambioSezione");
            PendingIntent pCambioSezione= PendingIntent.getActivity(ctx, 2, cambioSezione, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.imgCambioSezione, pCambioSezione);
        } else {
            Log.getInstance().ScriveLog("Set Listeners tasti. View NON corretta" );
        }
    }
}

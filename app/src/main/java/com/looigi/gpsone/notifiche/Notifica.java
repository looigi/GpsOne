package com.looigi.gpsone.notifiche;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.MainActivity;
import com.looigi.gpsone.R;
import com.looigi.gpsone.Utility;
import com.looigi.gpsone.VariabiliGlobali;

import java.time.temporal.ValueRange;

public class Notifica {
    private static Notifica instance = null;

    private Notifica() {
    }

    public static Notifica getInstance() {
        if (instance == null) {
            instance = new Notifica();
        }

        return instance;
    }

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private Context context;
    private String Titolo;
    private final int NOTIF_ID = 727272;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public void CreaNotifica() {
        String id = "id_gpsone"; // default_channel_id
        String title = "title_gps1"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        // NotificationCompat.Builder builder;

        if (context == null) {
            context = MainActivity.getAppContext();
            if (context == null) {
                context = MainActivity.getAppActivity();
            }
        }

        if (context != null) {
            if (notificationManager == null) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_LOW);
                    mChannel.enableVibration(false);
                    notificationManager.createNotificationChannel(mChannel);
                }
                notificationBuilder = new NotificationCompat.Builder(context, id);

                intent = new Intent(context, PassaggioNotifica.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                RemoteViews contentView = new RemoteViews(MainActivity.getAppContext().getPackageName(),
                        R.layout.barra_notifica);
                setListenersTasti(contentView);
                setListeners(contentView);

                VariabiliGlobali.getInstance().setViewNotifica(contentView);

                notificationBuilder
                        .setContentTitle(Titolo)                            // required
                        .setSmallIcon(R.drawable.logo)   // required android.R.drawable.ic_media_play
                        .setContentText(context.getString(R.string.app_name)) // required
                        // .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        // .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                        // .setGroup("LOO'S WEB PLAYER")
                        // .setGroupSummary(true)
                        // .setDefaults(NotificationCompat.DEFAULT_ALL)
                        // .setPriority(NotificationManager.IMPORTANCE_LOW)
                        .setContentIntent(pendingIntent)
                        .setTicker("")
                        .setContent(contentView);

            }

            if (notificationBuilder != null) {
                /* if (VariabiliGlobali.getInstance().isGiaAggiornatoNotifica()) {
                    Notification notification = notificationBuilder.build();
                    notificationManager.notify(NOTIF_ID, notification);
                } else { */
                    VariabiliGlobali.getInstance().setNotification(notificationBuilder);
                // }
            }
        } else {
            Log.getInstance().ScriveLog("Set Listeners. Context non valido");
        }
    }

    private void setListeners(RemoteViews view){
        if (view != null) {
            Log.getInstance().ScriveLog("Set Listeners. View corretta");
        } else {
            Log.getInstance().ScriveLog("Set Listeners. View NON corretta");
            return;
        }

        view.setTextViewText(R.id.txtTitoloNotifica, "");
        view.setTextViewText(R.id.txtTitoloNotifica2, "");

        if (VariabiliGlobali.getInstance().isServizioGPS()) {
            view.setImageViewResource(R.id.imgPlayStop, R.drawable.icona_stop);
        } else {
            view.setImageViewResource(R.id.imgPlayStop, R.drawable.icona_play);
        }
    }

    public void AggiornaNotifica() {
        setListenersTasti(VariabiliGlobali.getInstance().getViewNotifica());
        if (notificationManager!=null && notificationBuilder != null) {
            // Log.getInstance().ScriveLog("Aggiorna notifica");
            notificationManager.notify(NOTIF_ID, notificationBuilder.build());
        }
    }

    private void setListenersTasti(RemoteViews view){
        if (view != null) {
            Log.getInstance().ScriveLog("Set Listeners tasti. View corretta" );

            /* Intent play=new Intent(context, PassaggioNotifica.class);
            play.putExtra("DO", "play");
            // play.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            PendingIntent pplay = PendingIntent.getActivity(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.imgPlay, pplay);
            // view.setImageViewResource(R.id.imgPlay, R.drawable.play);
            */

            Intent apre = new Intent(context, PassaggioNotifica.class);
            apre.putExtra("DO", "apre");
            PendingIntent pApre = PendingIntent.getActivity(context, 0, apre, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.layBarraNotifica, pApre);

            Intent playStop = new Intent(context, PassaggioNotifica.class);
            playStop.putExtra("DO", "playStop");
            PendingIntent pPlayStop= PendingIntent.getActivity(context, 1, playStop, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.imgPlayStop, pPlayStop);

            Intent cambioSezione = new Intent(context, PassaggioNotifica.class);
            cambioSezione.putExtra("DO", "cambioSezione");
            PendingIntent pCambioSezione= PendingIntent.getActivity(context, 2, cambioSezione, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.imgCambioSezione, pCambioSezione);
        } else {
            Log.getInstance().ScriveLog("Set Listeners tasti. View NON corretta" );
        }
    }

    public void RimuoviNotifica() {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        Log.getInstance().ScriveLog("Rimuovi notifica");
        if (notificationManager!=null) {
            try {
                notificationManager.cancel(NOTIF_ID);
                notificationManager = null;
                notificationBuilder = null;
                // NOTIF_ID++;
            } catch (Exception e) {
                Log.getInstance().ScriveLog(Utility.getInstance().PrendeErroreDaException(e));
            }
        }
    }
}

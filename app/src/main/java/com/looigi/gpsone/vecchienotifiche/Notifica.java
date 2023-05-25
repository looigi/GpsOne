/* package com.looigi.gpsone.notifiche;

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
    private String Titolo2;
    private Boolean CeSegnale = false;
    private final int NOTIF_ID = 727272;
    private RemoteViews contentView;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public void setCeSegnale(Boolean ceSegnale) { CeSegnale = ceSegnale; }

    public void setTitolo2(String titolo) {
        Titolo2 = titolo;
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
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder = new NotificationCompat.Builder(context, id);

                intent = new Intent(context, PassaggioNotifica.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                contentView = new RemoteViews(MainActivity.getAppContext().getPackageName(),
                        R.layout.barra_notifica);
                setListenersTasti(contentView);
                setListeners(contentView);

                // VariabiliGlobali.getInstance().setViewNotifica(contentView);

                notificationBuilder
                        .setContentTitle(Titolo)                            // required
                        .setSmallIcon(R.drawable.logo)   // required android.R.drawable.ic_media_play
                        .setContentText(context.getString(R.string.app_name)) // required
                        // .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setOnlyAlertOnce(false)
                        // .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                        // .setGroup("LOO'S WEB PLAYER")
                        // .setGroupSummary(true)
                        // .setDefaults(NotificationCompat.DEFAULT_ALL)
                        // .setPriority(NotificationManager.IMPORTANCE_LOW)
                        .setContentIntent(pendingIntent)
                        .setTicker("")
                        .setContent(contentView);

            // }

            if (notificationBuilder != null) {
                    VariabiliGlobali.getInstance().setNotification(notificationBuilder);
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

        view.setTextViewText(R.id.txtTitoloNotifica, Titolo);
        view.setTextViewText(R.id.txtTitoloNotifica2, Titolo2);
        if (CeSegnale) {
            view.setViewVisibility(R.id.imgCeSegnale, LinearLayout.VISIBLE);
        } else {
            view.setViewVisibility(R.id.imgCeSegnale, LinearLayout.GONE);
        }

        if (VariabiliGlobali.getInstance().isServizioGPS()) {
            view.setImageViewResource(R.id.imgPlayStop, R.drawable.icona_stop);
        } else {
            view.setImageViewResource(R.id.imgPlayStop, R.drawable.icona_play);
        }
    }

    private int contatoreAggiornamenti = 0;

    public void AggiornaNotifica() {
        Log.getInstance().ScriveLog("Aggiorna notifica. Contatore: " + contatoreAggiornamenti);

        if (contatoreAggiornamenti == 3) {
            contatoreAggiornamenti = 0;
            Log.getInstance().ScriveLog("Aggiorna notifica. Superato contatore. Ricreo la notifica");

            RimuoviNotifica();
            CreaNotifica();
        } else {
            contatoreAggiornamenti++;
            if (contentView != null) {
                setListeners(contentView); // VariabiliGlobali.getInstance().getViewNotifica());
                setListenersTasti(contentView); // VariabiliGlobali.getInstance().getViewNotifica());

                if (notificationManager != null && notificationBuilder != null) {
                    // Log.getInstance().ScriveLog("Aggiorna notifica");
                    try {
                        notificationBuilder
                                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                                .setOnlyAlertOnce(false)
                                .setTicker("")
                                .setContent(contentView);

                        notificationManager.notify(NOTIF_ID, notificationBuilder.build());
                    } catch (Exception e) {
                        Log.getInstance().ScriveLog("Aggiorna notifica errore: " + Utility.getInstance().PrendeErroreDaException(e));
                    }
                } else {
                    Log.getInstance().ScriveLog("Aggiorna notifica errore di notificationManager null");
                }
            } else {
                Log.getInstance().ScriveLog("Aggiorna notifica errore di contentView null");
            }
        }
    }

    private void setListenersTasti(RemoteViews view){
        if (view != null) {
            Log.getInstance().ScriveLog("Set Listeners tasti. View corretta" );

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
*/
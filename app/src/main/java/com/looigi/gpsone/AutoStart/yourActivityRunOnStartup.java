package com.looigi.gpsone.AutoStart;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.looigi.gpsone.Log;
import com.looigi.gpsone.MainActivity;

public class yourActivityRunOnStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /* if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } */

        Toast.makeText(context,"Faccio partire GPS One", Toast.LENGTH_SHORT).show();

        Log.getInstance().ScriveLog("*************ON RECEIVE BOOT*************");
        Log.getInstance().ScriveLog("Action: " + Intent.ACTION_BOOT_COMPLETED);
        Log.getInstance().ScriveLog("*************ON RECEIVE BOOT*************");

        /* Intent myIntent = new Intent(context, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent); */

        Intent i = new Intent();
        i.setClassName("com.looigi.gpsone", "com.looigi.gpsone.MainActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        /* int interval = 100;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, pi); */
    }
}
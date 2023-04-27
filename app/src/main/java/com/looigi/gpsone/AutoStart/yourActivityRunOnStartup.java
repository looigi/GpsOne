package com.looigi.gpsone.AutoStart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.looigi.gpsone.MainActivity;

public class yourActivityRunOnStartup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
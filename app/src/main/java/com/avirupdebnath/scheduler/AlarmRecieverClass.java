package com.avirupdebnath.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmRecieverClass extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
        int state=intent.getIntExtra("on_off",0);
        int id=intent.getIntExtra("id",0);

        //create intent for ringtone service
        Intent serviceIntent=new Intent(context,RingtoneService.class);

        //add state of the intent to serviceIntent
        serviceIntent.putExtra("state",state);
        serviceIntent.putExtra("id",id);
        //start intent
        context.startService(serviceIntent);
    }
}

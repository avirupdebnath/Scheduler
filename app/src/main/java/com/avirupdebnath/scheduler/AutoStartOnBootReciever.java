package com.avirupdebnath.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Avirup on 4/29/2017.
 */
public class AutoStartOnBootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Intent serviceIntent=new Intent(context,com.avirupdebnath.scheduler.AlarmResetOnBoot.class);
            serviceIntent.putExtra("ref",1);
            context.startService(serviceIntent);
        }
    }
}

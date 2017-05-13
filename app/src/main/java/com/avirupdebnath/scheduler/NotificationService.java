package com.avirupdebnath.scheduler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Avirup on 4/30/2017.
 */

public class NotificationService extends Service {
    private int id;
    NotificationManager notificationManager;
    SQLiteDatabase myDatabase;
    Cursor cursor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int idx=intent.getIntExtra("id",0);
        id=(idx>0)?idx:-idx;
        String message="Default";
        String subText="Default";
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        TodoItemDatabase handler=new TodoItemDatabase(getBaseContext());
        myDatabase=handler.getWritableDatabase();
        String[] FROM={"_id","day","class_name","batch_name","location","start_time","end_time","enable"};
        cursor=myDatabase.query("Demo",FROM,"_id="+id,null,null,null,null);
        cursor.moveToFirst();
        if (idx > 0) {
                message = "You Have a " + cursor.getString(2) + " class in 10 mins!";
                subText = "For " + cursor.getString(3) + " in " + cursor.getString(4) + "\nClick here to dismiss Notification!";
        }
        else {
                message = "Your class ends in 10 mins!";
                subText = "Click here to dismiss Notification!";
        }
        Toast.makeText(this,message+","+subText,Toast.LENGTH_SHORT).show();
        //setup the intent stop the ongoing alarm
        Intent stop_alarm =new Intent(this,RingtoneService.class);
        stop_alarm.putExtra("state",0);
        Log.e("Idx", String.valueOf(idx));
        stop_alarm.putExtra("id",idx);

        //setup the pending intent to go to the intent when alarm goes off
        PendingIntent myPendingIntent=PendingIntent.getService(this,0,stop_alarm,PendingIntent.FLAG_UPDATE_CURRENT);

        //build the notification
        Notification notification=new Notification.Builder(this)
                .setContentTitle(message)
                .setContentText(subText)
                .setContentIntent(myPendingIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(0,notification);
        return START_NOT_STICKY;
    }
}

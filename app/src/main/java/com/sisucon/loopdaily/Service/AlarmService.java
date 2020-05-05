package com.sisucon.loopdaily.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.sisucon.loopdaily.Activity.ClockAlarmActivity;
import com.sisucon.loopdaily.Activity.LoginActivity;
import com.sisucon.loopdaily.R;
import com.sisucon.loopdaily.lib.AlarmManagerUtils;


/**
 * Description: 定时提醒服务类
 * Author: Jack Zhang
 * create on: 2019-05-28 11:34
 */
public class AlarmService extends Service
{
    public static final String ACTION = "com.jz.alarmsample.alarm";

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Context context = getApplicationContext();
        long intervalMillis = intent.getLongExtra(AlarmManagerUtils.INTERVAL_MILLIS, 0);
        if (intervalMillis != 0)
            AlarmManagerUtils.setAlarmTime(context, intent.getLongExtra("time",0), intent);
        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        Intent intent_main = new Intent(context, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,0,intent_main, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notifitacionManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new  NotificationCompat.Builder(context, "notification")
                .setContentTitle("提醒")
            .setContentText(intent.getStringExtra(AlarmManagerUtils.TIPS))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.icon_loop)
            .setContentIntent(contentIntent)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.loop_icon))
            .setAutoCancel(true)
            .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notifitacionManager.notify(100, notification);
        clockIntent.putExtra(AlarmManagerUtils.ID, intent.getIntExtra(AlarmManagerUtils.ID, 0));
        clockIntent.putExtra(AlarmManagerUtils.TIPS, intent.getStringExtra(AlarmManagerUtils.TIPS));
        clockIntent.putExtra(AlarmManagerUtils.SOUND_OR_VIBRATOR, intent.getIntExtra(AlarmManagerUtils.SOUND_OR_VIBRATOR, 0));
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
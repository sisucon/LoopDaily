package com.sisucon.loopdaily.Receiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.sisucon.loopdaily.Activity.LoginActivity
import com.sisucon.loopdaily.R

class AlarmReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent_main = Intent(context,LoginActivity::class.java)
        val contentIntent = PendingIntent.getActivity(context,0,intent_main, PendingIntent.FLAG_UPDATE_CURRENT)
        val notifitacionManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification =  NotificationCompat.Builder(context, "notification")
            .setContentTitle(intent!!.getStringExtra("channelTitle"))
            .setContentText(intent!!.getStringExtra("channelText"))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.icon_loop)
            .setContentIntent(contentIntent)
            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.loop_icon))
            .setAutoCancel(true)
            .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL
        notifitacionManager.notify(100, notification)
    }
}
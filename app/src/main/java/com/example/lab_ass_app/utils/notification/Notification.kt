package com.example.lab_ass_app.utils.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.lab_ass_app.R
import com.example.lab_ass_app.utils.Constants

class Notification : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = NotificationCompat.Builder(context!!, Constants.channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent!!.getStringExtra(Constants.titleExtra))
            .setContentText(intent.getStringExtra(Constants.messageExtra))
            .build()

        val manager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Constants.notificationID, notification)
    }
}
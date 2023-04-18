package com.example.musicapp.utils

import com.example.musicapp.BuildConfig
import com.example.musicapp.R

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.fragment.app.FragmentActivity
import com.example.musicapp.MainActivity
import com.example.musicapp.broadcast_receiver.NextActionReceiver
import com.example.musicapp.broadcast_receiver.PauseActionReceiver
import com.example.musicapp.broadcast_receiver.PreviousActionReceiver
import com.example.musicapp.model.Music


private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context, index : Int, musicList : List<Music>, isPlaying : Boolean) {
    val music = musicList[index]
    val list : ArrayList<Music> = arrayListOf()
    list.addAll(musicList)
    val currentMusic : ArrayList<Music> = arrayListOf()
    currentMusic.add(music)
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
    ) {
        val name = "Music App"
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

//    build the notification object with the data to be shown
    notificationManager.cancelAll();
    val pauseIntent = Intent(context, PauseActionReceiver::class.java)
    pauseIntent.putExtra("isPlaying", isPlaying)
    pauseIntent.putExtra("index", "$index")
    pauseIntent.putExtra("currentMusic", music)
    pauseIntent.putParcelableArrayListExtra("musicList", list)
    val pendingPauseIntent = PendingIntent.getBroadcast(context, 1, pauseIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    val nextIntent = Intent(context, NextActionReceiver::class.java)
    nextIntent.putExtra("index", "$index")
    nextIntent.putExtra("currentMusic", music)
    nextIntent.putParcelableArrayListExtra("musicList", list)
    val pendingNextIntent = PendingIntent.getBroadcast(context, 2, nextIntent, PendingIntent.FLAG_CANCEL_CURRENT)

    val previousIntent = Intent(context, PreviousActionReceiver::class.java)
    previousIntent.putExtra("index", "$index")
    previousIntent.putExtra("currentMusic", music)
    previousIntent.putParcelableArrayListExtra("musicList", list)
    val pendingPreviousIntent = PendingIntent.getBroadcast(context, 3, previousIntent, PendingIntent.FLAG_CANCEL_CURRENT)

//    val previousIntent = PreviousActionReceiver.newIntent(context, index, list)
//    val stackBuilder = TaskStackBuilder.create(context)
//        .addParentStack(PreviousActionReceiver::class.java)
//        .addNextIntent(previousIntent)
//    val pendingPreviousIntent =stackBuilder
//        .getPendingIntent(3 , PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_queue_music_24)
        .setContentTitle("${music.title} index $index")
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setPriority(Notification.PRIORITY_LOW)
        .addAction(R.drawable.ic_baseline_skip_previous_32, "Previous", pendingPreviousIntent)
        .addAction(R.drawable.ic_baseline_pause_circle_filled_24, "Pause/Play", pendingPauseIntent)
        .addAction(R.drawable.ic_baseline_skip_next_32, "Next", pendingNextIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(getUniqueId(), notification)
}


private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())
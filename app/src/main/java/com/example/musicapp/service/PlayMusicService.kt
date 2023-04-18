package com.example.musicapp.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log

class PlayMusicService : Service() {
    var mediaPlayer : MediaPlayer? = MediaPlayer()
    var length : Int? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent?.extras
        if (extras == null){
            Log.d("Service", "Null")
        }
        else{
            Log.d("Service", "Not Null")
            val action = extras.getString("action")
            if (action == "play"){
                    val uriString = extras.getString("uri")
                    val uri = Uri.parse(uriString)
                    mediaPlayer?.apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(this@PlayMusicService, uri)
                        prepare()
                        start()
                    }
            }
            else if(action == "pause"){
                mediaPlayer?.pause()
                length= mediaPlayer?.currentPosition
            }
            else if (action == "resume"){
                if (length != null){
                    mediaPlayer?.seekTo(length!!)
                    mediaPlayer?.start()
                }
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun stopService(name: Intent?): Boolean {
        length = null
        return super.stopService(name)
    }
    override fun onDestroy() {
        super.onDestroy()
        length = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
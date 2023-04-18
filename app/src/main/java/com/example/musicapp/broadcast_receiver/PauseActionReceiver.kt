package com.example.musicapp.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import com.example.musicapp.R
import com.example.musicapp.model.Music
import com.example.musicapp.service.PlayMusicService
import com.example.musicapp.utils.sendNotification

class PauseActionReceiver() : BroadcastReceiver() {
    var handler : Handler? = null
    constructor(handler : Handler) : this(){
        this.handler = handler
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        var music: Music
        var indexString = intent?.getStringExtra("index")
        val list = intent?.getParcelableArrayListExtra<Music>("musicList")
        val musicList = list?.toMutableList()
        val currentMusic = intent?.getParcelableExtra<Music>("currentMusic")
        if (currentMusic == null)
            Toast.makeText(context, "Music Null", Toast.LENGTH_SHORT).show()
        var index = musicList?.indexOf(currentMusic)
        var isPlaying = intent?.getBooleanExtra("isPlaying", true)
        if (isPlaying != null){
            if (isPlaying){
                isPlaying = false
                val serviceIntent = Intent(context, PlayMusicService::class.java)
                serviceIntent.putExtra("action", "pause")
                context?.startService(serviceIntent)
                if (index != null && musicList != null)
                    sendNotification(context!!, index, musicList , isPlaying)
            }
            else{
                isPlaying = true
                val serviceIntent = Intent(context, PlayMusicService::class.java)
                serviceIntent.putExtra("action", "resume")
                context?.startService(serviceIntent)
                if (index != null && musicList != null)
                    sendNotification(context!!, index, musicList , isPlaying)
            }
        }

    }
}
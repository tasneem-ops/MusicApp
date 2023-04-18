package com.example.musicapp.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.musicapp.MainActivity
import com.example.musicapp.database.DBHelper
import com.example.musicapp.model.Music
import com.example.musicapp.service.PlayMusicService
import com.example.musicapp.utils.sendNotification

class PreviousActionReceiver() : BroadcastReceiver() {
    var handler : Handler? = null
    constructor(handler : Handler) : this(){
        this.handler = handler
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        var music : Music
        var indexString = intent?.getStringExtra("index")
        val list = intent?.getParcelableArrayListExtra<Music>("musicList")
        val currentMusic = intent?.getParcelableExtra<Music>("currentMusic")
        val musicList = list?.toMutableList()
        var index = musicList?.indexOf(currentMusic)
//        Toast.makeText(context, "Previous, current index = $index, current list size ${musicList?.size}", Toast.LENGTH_SHORT).show()

        if (index != null && musicList != null) {
            if ((index - 1) >= 0){
                index--
                music = musicList!![index]
                sendNotification(context!!, index, musicList , true)
                val serviceIntent = Intent(context, PlayMusicService::class.java)
                context?.stopService(serviceIntent)
                serviceIntent.putExtra("uri", music.uri.toString())
                serviceIntent.putExtra("action", "play")
                context?.startService(serviceIntent)
//                val bundle = Bundle()
//                bundle.putString("title", music.title)
//                bundle.putInt("index", index)
//                val message = Message()
//                message.data= bundle
//                handler?.sendMessage(message)
            }
            else
                return
        }

    }
    companion object{
        private const val EXTRA_Index = "index"
        private const val EXTRA_musicList = "musicList"

        fun newIntent(context: Context, index : Int, musicList : ArrayList<Music>): Intent {
            val intent = Intent(context, PreviousActionReceiver::class.java)
            intent.putExtra(EXTRA_Index, index)
            intent.putParcelableArrayListExtra(EXTRA_musicList, musicList)
            return intent
        }
    }

}
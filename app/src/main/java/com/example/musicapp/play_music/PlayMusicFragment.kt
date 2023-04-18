package com.example.musicapp.play_music

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper.prepare
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.broadcast_receiver.NextActionReceiver
import com.example.musicapp.broadcast_receiver.PreviousActionReceiver
import com.example.musicapp.database.DBHelper
import com.example.musicapp.databinding.FragmentPlayMusicBinding
import com.example.musicapp.model.Music
import com.example.musicapp.service.PlayMusicService
import com.example.musicapp.utils.sendNotification
import kotlinx.coroutines.NonCancellable.start


class PlayMusicFragment : Fragment() {
    private lateinit var binding : FragmentPlayMusicBinding
    private var isPlaying = false
    var musicList = mutableListOf<Music>()
    var index : Int = 0
    private lateinit var music : Music
    var title : String? = ""
//    val handler = object : Handler(){
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//            title = msg.data.getString("title")
//            index = msg.data.getInt("index")
//            binding.textTitle.text = title
//            Toast.makeText(requireContext(), "Title $title", Toast.LENGTH_LONG).show()
//        }
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlayMusicBinding.inflate(layoutInflater)
        musicList = PlayMusicFragmentArgs.fromBundle(requireArguments()).musicList.toMutableList()
        index = PlayMusicFragmentArgs.fromBundle(requireArguments()).index
        music = musicList[index]
        binding.music = music
        binding.nextMusic.setOnClickListener { playNextMusic(index) }
        binding.prevMusic.setOnClickListener { playPreviousMusic(index) }
        binding.pauseMusic.setOnClickListener { pauseMusic(music.uri) }
        binding.stopMusic.setOnClickListener { stopMusic() }

        val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
        requireContext().stopService(serviceIntent)
        playMusic(music.uri)
//        val nextActionReceiver = NextActionReceiver(handler)
//        val previousActionReceiver = PreviousActionReceiver(handler)
//        val pauseActionReceiver = PreviousActionReceiver(handler)
        return binding.root
    }

    private fun playNextMusic(i : Int){

        if ((musicList[index+1]) != null && ((index +1)< musicList.size)){
                index++
                music = musicList[index]
                title = music.title
                sendNotification(requireContext(), index, musicList, true )
                val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
                requireContext().stopService(serviceIntent)
                playMusic(music.uri)
                binding.textTitle.text = music.title
            }
        else
            return
    }
    private fun playPreviousMusic(i : Int){
        if ((index - 1) >= 0){
            index--
            music = musicList[index]
            title = music.title
            sendNotification(requireContext(), index, musicList , true)
            val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
            requireContext().stopService(serviceIntent)
            playMusic(music.uri)
            binding.textTitle.text = music.title
        }
        else
            return
    }
    private fun pauseMusic(uri: Uri){
        if (isPlaying){
            binding.pauseMusic.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
            isPlaying = false
            val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
            serviceIntent.putExtra("uri", uri.toString())
            serviceIntent.putExtra("action", "pause")
            requireContext().startService(serviceIntent)
        }
        else{
            resumeMusic(uri)
        }
    }
    private fun playMusic(uri : Uri){
        isPlaying = true
        binding.pauseMusic.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
        val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
        serviceIntent.putExtra("uri", uri.toString())
        serviceIntent.putExtra("action", "play")
        requireContext().startService(serviceIntent)
        }

    private fun resumeMusic(uri : Uri){
        isPlaying = true
        binding.pauseMusic.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24)
        val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
        serviceIntent.putExtra("uri", uri.toString())
        serviceIntent.putExtra("action", "resume")
        requireContext().startService(serviceIntent)
    }
    private fun stopMusic(){
        val serviceIntent = Intent(requireContext(), PlayMusicService::class.java)
        requireContext().stopService(serviceIntent)
        this.findNavController()
            .navigate(PlayMusicFragmentDirections.actionPlayMusicFragmentToMusicListFragment())
    }

    }

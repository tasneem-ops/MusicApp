package com.example.musicapp.music_list

import android.Manifest
import android.app.SearchManager
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.musicapp.MainActivity
import com.example.musicapp.R
import com.example.musicapp.database.DBHelper
import com.example.musicapp.databinding.FragmentMusicListBinding
import com.example.musicapp.model.Music
import com.example.musicapp.utils.sendNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class MusicListFragment : Fragment() {

    private lateinit var binding: FragmentMusicListBinding
    private lateinit var adapter: MusicListAdapter
    var musicList = mutableListOf<Music>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMusicListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MusicListAdapter(MusicListListener {
            val index = musicList.indexOf(it)
            sendNotification(requireContext(), index, musicList , true)
            Toast.makeText(requireContext(), "Playing audio in background.", Toast.LENGTH_SHORT).show()
            this.findNavController()
                .navigate(MusicListFragmentDirections
                    .actionMusicListFragmentToPlayMusicFragment(musicList.toTypedArray(), index))
        })
        binding.lifecycleOwner = this
        binding.musicList.adapter = adapter
        requestStoragePermissions()

        val db = DBHelper(requireContext(), null)
        binding.btnSearch.setOnClickListener {
            val searchOutputList = mutableListOf<Music>()
            val searchText = binding.editText.text.toString()
            val cursor = db.getSearchOutput(searchText)
            if (cursor == null){
                Toast.makeText(requireContext(), "No Data", Toast.LENGTH_SHORT).show()
            }
            else{
                while (cursor.moveToNext()){
                    val id = cursor.getLong(0)
                    val title = cursor.getString(1)
                    val uriString = cursor.getString(2)
                    val uri = Uri.parse(uriString)
                    val duration = cursor.getString(3)
                    val album = cursor.getString(4)
                    searchOutputList.add(Music(title, uri, duration, album, id))
                }
            }

            adapter.submitList(searchOutputList)

        }
        lifecycleScope.launch {
            musicList = loadMusic().toMutableList()
            adapter.submitList(musicList)
            adapter.notifyDataSetChanged()
            musicList.forEach { music ->
                db.addRecord(music.title, music.uri.toString(), music.duration, music.album , music.id)
            }
        }
    }

    private fun requestStoragePermissions(){
        if(!checkStoragePermission()){
            val readPermission = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val permissionsArray = mutableListOf<String>()
            if (!readPermission)
                permissionsArray.add(Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionsArray.isNotEmpty()){
                ActivityCompat.requestPermissions(requireActivity(), permissionsArray.toTypedArray(), requestCodeStorage)
            }
        }

    }

    private fun checkStoragePermission(): Boolean{
        val readPermission = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return readPermission
    }

    private suspend fun loadMusic(): List<Music>{
        return withContext(Dispatchers.IO){

            val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM)

            val music = mutableListOf<Music>()

            requireContext().contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Audio.Media.DURATION} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

                while (cursor.moveToNext()){
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn)
                    val duration = cursor.getString(durationColumn)
//                    duration = convertTime(duration)

                    val album = cursor.getString(albumColumn)

                    val contentUri : Uri = ContentUris.appendId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon(),
                        id
                    ).build()
                    music.add(Music(title, contentUri, "22", album, id))
                }
                music.toList()
            }?: listOf()
        }
    }

    fun convertTime(duration: String): String {
        val millis = duration.toLong()
        return java.lang.String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
        )
    }

}

val requestCodeStorage = 1
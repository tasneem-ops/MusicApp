package com.example.musicapp.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Music(
    val title : String,
    val uri : Uri,
    val duration : String,
    val album : String,
    var id : Long
) : Parcelable

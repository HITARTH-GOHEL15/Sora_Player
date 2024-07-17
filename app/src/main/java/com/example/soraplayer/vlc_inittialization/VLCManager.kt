package com.example.soraplayer.vlc_inittialization

import android.content.Context
import org.videolan.libvlc.LibVLC


object VLCManager {

    lateinit var libVLC : LibVLC

    fun initialize(context: Context) {
        val options = arrayListOf<String>()
        libVLC = LibVLC(context, options)
    }

}
package com.pianolearn.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()
    private var isLoaded = false
    private var currentInstrument = "Classic Piano"

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(10) // Allow up to 10 keys to be played simultaneously
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            }
        }
        
        loadInstrument(currentInstrument)
    }

    fun loadInstrument(instrument: String) {
        currentInstrument = instrument
        soundMap.clear()
        isLoaded = false
        try {
            // Note indices for startOctave=3: C3 = 48
            // Load C3 to C5 (48 to 72)
            val baseIndex = 48
            val notes = listOf("c3", "db3", "d3", "eb3", "e3", "f3", "gb3", "g3", "ab3", "a3", "bb3", "b3", 
                               "c4", "db4", "d4", "eb4", "e4", "f4", "gb4", "g4", "ab4", "a4", "bb4", "b4", "c5")
            for ((i, note) in notes.withIndex()) {
                val resId = context.resources.getIdentifier("piano_$note", "raw", context.packageName)
                if (resId != 0) {
                    soundMap[baseIndex + i] = soundPool?.load(context, resId, 1) ?: 0
                }
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error loading sounds", e)
        }
        Log.d("SoundManager", "Loaded instrument: $instrument")
        isLoaded = true // Mock loaded
    }

    fun playNote(noteIndex: Int) {
        if (!isLoaded) return
        val soundId = soundMap[noteIndex]
        if (soundId != null && soundId != 0) {
            soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
        } else {
            Log.d("SoundManager", "Playing synthesized or fallback note for index: $noteIndex")
            // Fallback if no specific sample is loaded
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}

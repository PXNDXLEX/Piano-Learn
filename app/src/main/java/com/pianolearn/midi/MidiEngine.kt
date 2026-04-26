package com.pianolearn.midi

import com.leff.midi.MidiFile
import com.leff.midi.event.NoteOn
import com.leff.midi.event.NoteOff
import com.pianolearn.ui.components.FallingNote
import java.io.File
import java.io.InputStream

class MidiEngine {

    fun parseMidi(inputStream: InputStream): List<FallingNote> {
        val fallingNotes = mutableListOf<FallingNote>()
        try {
            val midi = MidiFile(inputStream)
            val resolution = midi.resolution

            // Map to store note start times
            val activeNotes = mutableMapOf<Int, Long>() // NoteIndex -> StartTick
            
            // Assume 120 BPM for now, real implementation would parse tempo events
            val ticksPerQuarter = resolution
            val microsecondsPerQuarter = 500000L // 120 BPM
            val msPerTick = (microsecondsPerQuarter / 1000.0) / ticksPerQuarter

            for (track in midi.tracks) {
                val trackIndex = midi.tracks.indexOf(track)
                val it = track.events.iterator()
                while (it.hasNext()) {
                    val event = it.next()
                    if (event is NoteOn && event.velocity > 0) {
                        activeNotes[event.noteValue] = event.tick
                    } else if (event is NoteOff || (event is NoteOn && event.velocity == 0)) {
                        val noteValue = if (event is NoteOff) event.noteValue else (event as NoteOn).noteValue
                        val startTick = activeNotes.remove(noteValue)
                        if (startTick != null) {
                            val durationTicks = event.tick - startTick
                            val startTimeMs = (startTick * msPerTick).toLong()
                            val durationMs = (durationTicks * msPerTick).toLong()
                            fallingNotes.add(FallingNote(noteValue, startTimeMs, durationMs, trackIndex))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return fallingNotes.sortedBy { it.startTime }
    }
}

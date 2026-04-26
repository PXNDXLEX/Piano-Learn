package com.pianolearn.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

data class FallingNote(
    val noteIndex: Int,
    val startTime: Long, // in ms
    val duration: Long,  // in ms
    val trackIndex: Int = 0
)

@Composable
fun MidiVisualizer(
    modifier: Modifier = Modifier,
    fallingNotes: List<FallingNote>,
    currentTime: Long,
    timeWindowMs: Long = 3000L, // How many ms of notes to show on screen
    startOctave: Int = 3,
    numOctaves: Int = 2
) {
    val totalKeys = numOctaves * 12
    val whiteKeysCount = totalKeys - (numOctaves * 5) // 7 white keys per octave

    Canvas(modifier = modifier
        .fillMaxSize()
        .background(Color(0xFF1E1E1E))) { // Dark background for contrast
        val whiteKeyWidth = size.width / whiteKeysCount

        fallingNotes.forEach { note ->
            val timeToReach = note.startTime - currentTime
            val timeEnd = (note.startTime + note.duration) - currentTime

            if (timeEnd > 0 && timeToReach < timeWindowMs) {
                // Calculate position mapping note index to X coordinate
                val relativeNote = note.noteIndex - (startOctave * 12)
                if (relativeNote in 0 until totalKeys) {
                    // Determine X position based on key layout
                    var whiteIndex = 0
                    var isBlack = false
                    for (i in 0 until relativeNote) {
                        val n = i % 12
                        if (n == 1 || n == 3 || n == 6 || n == 8 || n == 10) {
                            // black
                        } else {
                            whiteIndex++
                        }
                    }
                    val n = relativeNote % 12
                    isBlack = n == 1 || n == 3 || n == 6 || n == 8 || n == 10

                    val x = if (isBlack) {
                        (whiteKeyWidth * whiteIndex) - (whiteKeyWidth * 0.3f)
                    } else {
                        whiteKeyWidth * whiteIndex
                    }
                    val w = if (isBlack) whiteKeyWidth * 0.6f else whiteKeyWidth

                    // Calculate Y position
                    // Bottom is currentTime = 0 (reaching the keys)
                    val yBottom = size.height - (timeToReach.toFloat() / timeWindowMs.toFloat() * size.height)
                    val yTop = size.height - (timeEnd.toFloat() / timeWindowMs.toFloat() * size.height)

                    val color = if (note.trackIndex == 0) Color(0xFF00E5FF) else Color(0xFFFF4081)

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, yTop),
                        size = Size(w, yBottom - yTop),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }
        
        // Draw bottom line representing keys
        drawLine(
            color = Color.Red,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2.dp.toPx()
        )
    }
}

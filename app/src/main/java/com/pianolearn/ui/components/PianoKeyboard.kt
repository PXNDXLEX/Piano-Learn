package com.pianolearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PianoKey(val noteIndex: Int, val isBlack: Boolean, val label: String = "")

@Composable
fun PianoKeyboard(
    modifier: Modifier = Modifier,
    startOctave: Int = 3,
    numOctaves: Int = 2,
    activeNotes: Set<Int>,
    onNotePressed: (Int) -> Unit,
    onNoteReleased: (Int) -> Unit
) {
    // Generate keys
    val keys = remember(startOctave, numOctaves) {
        val list = mutableListOf<PianoKey>()
        for (i in 0 until numOctaves * 12) {
            val note = i % 12
            val isBlack = note == 1 || note == 3 || note == 6 || note == 8 || note == 10
            val label = if (note == 0) "C${startOctave + i / 12}" else ""
            list.add(PianoKey(startOctave * 12 + i, isBlack, label))
        }
        list
    }

    val whiteKeys = keys.filter { !it.isBlack }
    
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val whiteKeyWidth = maxWidth / whiteKeys.size
        val whiteKeyHeight = maxHeight
        val blackKeyWidth = whiteKeyWidth * 0.6f
        val blackKeyHeight = maxHeight * 0.65f

        // Draw White Keys
        Row(modifier = Modifier.fillMaxSize()) {
            whiteKeys.forEach { key ->
                val isActive = activeNotes.contains(key.noteIndex)
                Box(
                    modifier = Modifier
                        .width(whiteKeyWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 0.5.dp)
                        .shadow(4.dp, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(if (isActive) Color.Cyan else Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .pointerInput(key.noteIndex) {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                onNotePressed(key.noteIndex)
                                do {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { it.consume() }
                                } while (event.changes.any { it.pressed })
                                onNoteReleased(key.noteIndex)
                            }
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (key.label.isNotEmpty()) {
                        Text(text = key.label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            }
        }

        // Draw Black Keys
        var whiteIndex = 0
        keys.forEach { key ->
            if (key.isBlack) {
                val offset = (whiteKeyWidth * whiteIndex) - (blackKeyWidth / 2)
                val isActive = activeNotes.contains(key.noteIndex)
                Box(
                    modifier = Modifier
                        .offset(x = offset)
                        .width(blackKeyWidth)
                        .height(blackKeyHeight)
                        .shadow(6.dp, RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(if (isActive) Color(0xFF00BFFF) else Color.Black)
                        .pointerInput(key.noteIndex) {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                onNotePressed(key.noteIndex)
                                do {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { it.consume() }
                                } while (event.changes.any { it.pressed })
                                onNoteReleased(key.noteIndex)
                            }
                        }
                )
            } else {
                whiteIndex++
            }
        }
    }
}

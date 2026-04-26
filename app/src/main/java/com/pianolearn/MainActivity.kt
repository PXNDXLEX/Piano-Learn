package com.pianolearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pianolearn.ui.components.FallingNote
import com.pianolearn.ui.components.MidiVisualizer
import com.pianolearn.ui.components.PianoKeyboard
import com.pianolearn.ui.components.TopNavigation
import com.pianolearn.audio.SoundManager
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundManager = SoundManager(this)
        
        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }
            
            MaterialTheme(
                colorScheme = if (isDarkTheme) androidx.compose.material3.darkColorScheme(
                    primary = Color(0xFF00E5FF),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E)
                ) else androidx.compose.material3.lightColorScheme(
                    primary = Color(0xFF00838F),
                    background = Color(0xFFF5F5F5),
                    surface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PianoAppScreen(
                        soundManager = soundManager,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun PianoLearnTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}

@Composable
fun PianoAppScreen(
    soundManager: SoundManager,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var activeNotes by remember { mutableStateOf(setOf<Int>()) }
    var currentInstrument by remember { mutableStateOf("Classic Piano") }
    var currentTimeMs by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }

    // Mock falling notes for demonstration
    val mockNotes = remember {
        listOf(
            FallingNote(48, 1000, 500), // C3
            FallingNote(50, 1500, 500), // D3
            FallingNote(52, 2000, 500), // E3
            FallingNote(53, 2500, 500), // F3
            FallingNote(55, 3000, 1000) // G3
        )
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val startTime = System.currentTimeMillis() - currentTimeMs
            while (true) {
                currentTimeMs = System.currentTimeMillis() - startTime
                // Highlight keys that are currently "hit" by falling notes
                val currentlyActive = mockNotes.filter {
                    currentTimeMs >= it.startTime && currentTimeMs <= (it.startTime + it.duration)
                }.map { it.noteIndex }.toSet()
                
                // Add user pressed notes
                // activeNotes = activeNotes + currentlyActive (Simplified logic)
                
                delay(16) // ~60fps
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        
        // Add a simple top bar row for Theme toggle
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onThemeToggle) {
                Text(if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode", color = MaterialTheme.colorScheme.primary)
            }
        }

        TopNavigation(
            onOpenLibrary = { isPlaying = !isPlaying }, // Toggle play for mock testing
            onOpenSettings = { },
            currentInstrument = currentInstrument,
            onInstrumentClick = {
                currentInstrument = if (currentInstrument == "Classic Piano") "Modern Synth" else "Classic Piano"
                soundManager.loadInstrument(currentInstrument)
            }
        )

        Box(modifier = Modifier.weight(1f)) {
            MidiVisualizer(
                fallingNotes = mockNotes,
                currentTime = currentTimeMs,
                startOctave = 3,
                numOctaves = 2
            )
        }

        PianoKeyboard(
            modifier = Modifier.height(150.dp),
            startOctave = 3,
            numOctaves = 2,
            activeNotes = activeNotes,
            onNotePressed = { note ->
                activeNotes = activeNotes + note
                soundManager.playNote(note)
            },
            onNoteReleased = { note ->
                activeNotes = activeNotes - note
                // SoundPool doesn't easily stop specific notes without stream IDs, 
                // but usually piano notes decay naturally.
            }
        )
    }
}

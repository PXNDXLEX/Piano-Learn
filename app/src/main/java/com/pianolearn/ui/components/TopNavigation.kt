package com.pianolearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigation(
    modifier: Modifier = Modifier,
    onOpenLibrary: () -> Unit,
    onOpenSettings: () -> Unit,
    currentInstrument: String,
    onInstrumentClick: () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF2C2C2C))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Instrument Selector
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF424242))
                .clickable { onInstrumentClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = "Instrument: $currentInstrument", color = Color.White)
        }

        // Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = onOpenLibrary) {
                Icon(Icons.Default.List, contentDescription = "MIDI Library", tint = Color.White)
            }
            IconButton(onClick = { showTooltip = true }) {
                Icon(Icons.Default.Info, contentDescription = "Help", tint = Color.White)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }
    }

    if (showTooltip) {
        AlertDialog(
            onDismissRequest = { showTooltip = false },
            title = { Text("How to use") },
            text = { Text("Use the Library button to load local or public MIDIs. Switch instruments clicking on the Instrument badge. The notes will fall and light up the keys.") },
            confirmButton = {
                TextButton(onClick = { showTooltip = false }) {
                    Text("Got it")
                }
            }
        )
    }
}

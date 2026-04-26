package com.pianolearn.midi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MidiSearchResult(val name: String, val downloadUrl: String)

class PublicMidiRepository {
    
    // In a full implementation, you would use Jsoup to scrape BitMidi or another database
    // implementation("org.jsoup:jsoup:1.17.2")
    
    suspend fun searchMidis(query: String): List<MidiSearchResult> = withContext(Dispatchers.IO) {
        // Mocking results for now as a recommended base (BitMidi) needs JSoup scraping
        // val doc = Jsoup.connect("https://bitmidi.com/search?q=$query").get()
        // parse doc and return results...
        
        delay(1000) // Simulating network
        listOf(
            MidiSearchResult("Fur Elise - Beethoven", "https://example.com/fur_elise.mid"),
            MidiSearchResult("Moonlight Sonata - Beethoven", "https://example.com/moonlight.mid"),
            MidiSearchResult("Clair de Lune - Debussy", "https://example.com/clair.mid")
        )
    }
}

package com.example.aura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.aura.data.remote.GeminiServiceImpl
import com.example.aura.data.repository.AuraRepository
import com.example.aura.ui.navigation.AuraNavGraph
import com.example.aura.ui.theme.AuraTheme

/**
 * Main entry point for the Aura app.
 *
 * Sets up the Gemini service, repository, and navigation graph.
 * For hackathon speed we create dependencies manually here.
 * In production, use Hilt for dependency injection.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ─── Initialize dependencies ─────────────────────
        // API key from BuildConfig (set in local.properties)
        val apiKey = BuildConfig.GEMINI_API_KEY

        val geminiService = GeminiServiceImpl(apiKey)
        val repository = AuraRepository(geminiService)

        // ─── Set up UI ───────────────────────────────────
        setContent {
            AuraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuraNavGraph(repository = repository)
                }
            }
        }
    }
}
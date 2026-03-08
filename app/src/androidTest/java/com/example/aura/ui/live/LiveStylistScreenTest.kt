package com.example.aura.ui.live

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.data.model.ClothingItem
import com.example.aura.data.repository.AuraRepository
import com.example.aura.data.voice.VoiceService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class LiveStylistScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialScreen_showsCaptureButton_noMic() {
        val repository = mockk<AuraRepository>(relaxed = true) {
            every { outfitAnalysis } returns MutableStateFlow(null)
            every { chatMessages } returns MutableStateFlow(emptyList())
            every { isLoading } returns MutableStateFlow(false)
            every { error } returns MutableStateFlow(null)
        }
        val voiceService = mockk<VoiceService>(relaxed = true) {
            every { isListening } returns MutableStateFlow(false)
            every { isSpeaking } returns MutableStateFlow(false)
            every { partialText } returns MutableStateFlow("")
        }

        val viewModel = LiveStylistViewModel(repository, voiceService)

        composeTestRule.setContent {
            LiveStylistScreen(
                viewModel = viewModel,
                onViewTranscript = {}
            )
        }

        // Before analysis, Capture button is shown
        composeTestRule.onNodeWithContentDescription("Capture outfit").assertIsDisplayed()
        // Mic button and transcript button should NOT be shown yet
        composeTestRule.onNodeWithContentDescription("Start listening").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("View transcript").assertDoesNotExist()
    }

    @Test
    fun afterAnalysis_showsMicAndTranscriptButton_hidesCapture() {
        val mockAnalysis = OutfitAnalysis(
            items = listOf(ClothingItem("Blue Jeans", "bottom", "Blue", 0.9f)),
            overallStyle = "Casual",
            dominantColors = listOf("Blue"),
            summary = "Great casual look."
        )

        val repository = mockk<AuraRepository>(relaxed = true) {
            every { outfitAnalysis } returns MutableStateFlow(mockAnalysis)
            every { chatMessages } returns MutableStateFlow(emptyList())
            every { isLoading } returns MutableStateFlow(false)
            every { error } returns MutableStateFlow(null)
        }
        val voiceService = mockk<VoiceService>(relaxed = true) {
            every { isListening } returns MutableStateFlow(false)
            every { isSpeaking } returns MutableStateFlow(false)
            every { partialText } returns MutableStateFlow("")
        }

        val viewModel = LiveStylistViewModel(repository, voiceService)
        // Force state as if we called captureAndAnalyze
        viewModel.javaClass.getDeclaredField("_hasAnalyzed").apply {
            isAccessible = true
            (get(viewModel) as MutableStateFlow<Boolean>).value = true
        }

        composeTestRule.setContent {
            LiveStylistScreen(
                viewModel = viewModel,
                onViewTranscript = {}
            )
        }

        // Capture button should be gone
        composeTestRule.onNodeWithContentDescription("Capture outfit").assertDoesNotExist()
        
        // Mic button and View Transcript should be visible
        composeTestRule.onNodeWithContentDescription("Start listening").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("View transcript").assertIsDisplayed()
        
        // The outfit tag chip should be visible
        composeTestRule.onNodeWithText("Blue Jeans").assertIsDisplayed()
    }

    @Test
    fun whileListening_showsPartialTextAndStopMic() {
        val repository = mockk<AuraRepository>(relaxed = true) {
            every { outfitAnalysis } returns MutableStateFlow(null)
            every { chatMessages } returns MutableStateFlow(emptyList())
            every { isLoading } returns MutableStateFlow(false)
            every { error } returns MutableStateFlow(null)
        }
        val voiceService = mockk<VoiceService>(relaxed = true) {
            every { isListening } returns MutableStateFlow(true)
            every { isSpeaking } returns MutableStateFlow(false)
            every { partialText } returns MutableStateFlow("Do these match")
        }

        val viewModel = LiveStylistViewModel(repository, voiceService)
        // Force state as if analyzed
        viewModel.javaClass.getDeclaredField("_hasAnalyzed").apply {
            isAccessible = true
            (get(viewModel) as MutableStateFlow<Boolean>).value = true
        }

        composeTestRule.setContent {
            LiveStylistScreen(
                viewModel = viewModel,
                onViewTranscript = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Stop listening").assertIsDisplayed()
        composeTestRule.onNodeWithText("🎤 Do these match").assertIsDisplayed()
    }
}

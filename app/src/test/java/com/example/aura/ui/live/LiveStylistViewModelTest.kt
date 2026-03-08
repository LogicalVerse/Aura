package com.example.aura.ui.live

import android.graphics.Bitmap
import app.cash.turbine.test
import com.example.aura.data.model.ChatMessage
import com.example.aura.data.model.MessageRole
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.data.repository.AuraRepository
import com.example.aura.data.voice.VoiceService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LiveStylistViewModelTest {

    private lateinit var repository: AuraRepository
    private lateinit var voiceService: VoiceService
    private lateinit var viewModel: LiveStylistViewModel

    // Fake flows to back the repository mocks
    private val chatMessagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val outfitAnalysisFlow = MutableStateFlow<OutfitAnalysis?>(null)
    private val isLoadingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)

    // Fake flows for VoiceService
    private val voiceRecognizedFlow = MutableStateFlow("")
    private val voiceListeningFlow = MutableStateFlow(false)
    private val voiceSpeakingFlow = MutableStateFlow(false)
    private val voicePartialFlow = MutableStateFlow("")

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true) {
            every { chatMessages } returns chatMessagesFlow
            every { outfitAnalysis } returns outfitAnalysisFlow
            every { isLoading } returns isLoadingFlow
            every { error } returns errorFlow
        }

        voiceService = mockk(relaxed = true) {
            every { recognizedText } returns voiceRecognizedFlow
            every { isListening } returns voiceListeningFlow
            every { isSpeaking } returns voiceSpeakingFlow
            every { partialText } returns voicePartialFlow
        }

        viewModel = LiveStylistViewModel(repository, voiceService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `captureAndAnalyze sends to repository and speaks greeting`() = runTest {
        val bitmap = mockk<Bitmap>()
        val analysis = OutfitAnalysis(
            items = emptyList(),
            overallStyle = "Casual",
            dominantColors = emptyList(),
            summary = "Looking great today!"
        )

        // When analyzeOutfit is called, we simulate the repository updating its flow
        coEvery { repository.analyzeOutfit(bitmap) } answers {
            outfitAnalysisFlow.value = analysis
        }

        viewModel.captureAndAnalyze(bitmap)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.analyzeOutfit(bitmap) }
        verify { voiceService.speak("Looking great today!") }
        assertTrue(viewModel.hasAnalyzed.value)
        assertEquals("Looking great today!", viewModel.lastSpokenMessage.value)
    }

    @Test
    fun `startVoiceInput stops TTS and starts STT`() = runTest {
        viewModel.startVoiceInput()
        
        verify { voiceService.stopSpeaking() }
        verify { voiceService.startListening() }
    }

    @Test
    fun `sendMessage sends to repo and speaks AI response`() = runTest {
        val userMsg = "Is this for a date?"
        val aiResponse = ChatMessage(role = MessageRole.ASSISTANT, content = "Yes, absolutely.")

        coEvery { repository.sendMessage(userMsg) } answers {
            chatMessagesFlow.value = listOf(aiResponse)
        }

        viewModel.sendMessage(userMsg)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.sendMessage(userMsg) }
        verify { voiceService.speak("Yes, absolutely.") }
        assertEquals("Yes, absolutely.", viewModel.lastSpokenMessage.value)
    }

    @Test
    fun `voice recognition auto-sends message to repository`() = runTest {
        val aiResponse = ChatMessage(role = MessageRole.ASSISTANT, content = "Perfect.")
        
        coEvery { repository.sendMessage("Do these shoes match?") } answers {
            chatMessagesFlow.value = listOf(aiResponse)
        }

        // Simulate voice recognition completing
        voiceRecognizedFlow.value = "Do these shoes match?"
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.sendMessage("Do these shoes match?") }
        verify { voiceService.speak("Perfect.") }
    }
}

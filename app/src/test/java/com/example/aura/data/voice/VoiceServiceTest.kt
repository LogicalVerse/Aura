package com.example.aura.data.voice

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.ArrayList

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class VoiceServiceTest {

    private lateinit var context: Context
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private lateinit var voiceService: VoiceService

    // We capture the listener the VoiceService registers to simulate Android callbacks
    private val recognitionListenerSlot = slot<RecognitionListener>()

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        speechRecognizer = mockk(relaxed = true)
        tts = mockk(relaxed = true)

        // Mock Android static factory
        mockkStatic(SpeechRecognizer::class)
        every { SpeechRecognizer.createSpeechRecognizer(any()) } returns speechRecognizer
        every { speechRecognizer.setRecognitionListener(capture(recognitionListenerSlot)) } returns Unit

        // Mock Android TextToSpeech constructor
        mockkConstructor(TextToSpeech::class)

        voiceService = VoiceService(context)
    }

    @Test
    fun `startListening sets isListening to true`() = runTest {
        voiceService.isListening.test {
            assertFalse(awaitItem()) // initial state
            
            voiceService.startListening()
            
            // To properly mock this without robolectric, we simulate the Android callback
            recognitionListenerSlot.captured.onReadyForSpeech(Bundle())
            
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `stopListening sets isListening to false`() = runTest {
        // First start it
        voiceService.startListening()
        recognitionListenerSlot.captured.onReadyForSpeech(Bundle())
        
        voiceService.isListening.test {
            assertTrue(awaitItem()) // Current state is true
            
            voiceService.stopListening()
            verify { speechRecognizer.stopListening() }
            
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onResults updates recognizedText and partialText is cleared`() = runTest {
        voiceService.startListening()
        val listener = recognitionListenerSlot.captured

        voiceService.recognizedText.test {
            assertEquals("", awaitItem()) // initial

            // Simulate Android returning a STT match
            val bundle = mockk<Bundle>()
            val results = ArrayList<String>().apply { add("Hello Aura") }
            every { bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) } returns results

            listener.onResults(bundle)

            assertEquals("Hello Aura", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        // Also verify partial text was cleared
        assertEquals("", voiceService.partialText.value)
        // And it stopped listening
        assertFalse(voiceService.isListening.value)
    }

    @Test
    fun `onPartialResults updates partialText`() = runTest {
        voiceService.startListening()
        val listener = recognitionListenerSlot.captured

        voiceService.partialText.test {
            assertEquals("", awaitItem()) // initial

            val bundle = mockk<Bundle>()
            val partials = ArrayList<String>().apply { add("Hello Au") }
            every { bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) } returns partials

            listener.onPartialResults(bundle)

            assertEquals("Hello Au", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onError updates error state and stops listening`() = runTest {
        voiceService.startListening()
        val listener = recognitionListenerSlot.captured

        voiceService.error.test {
            assertEquals(null, awaitItem()) // initial

            listener.onError(SpeechRecognizer.ERROR_NO_MATCH)

            assertEquals("Didn't catch that — try again", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        
        assertFalse(voiceService.isListening.value)
    }
}

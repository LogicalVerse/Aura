package com.example.aura.data.repository

import android.graphics.Bitmap
import com.example.aura.data.model.ChatMessage
import com.example.aura.data.model.MessageRole
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.data.model.StylistResponse
import com.example.aura.data.remote.GeminiService
import com.example.aura.util.PromptTemplates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository that manages AI interactions and session state.
 *
 * Acts as the single source of truth for:
 * - Current outfit analysis
 * - Chat message history
 * - Loading states
 *
 * All ViewModels should go through this repository
 * rather than calling [GeminiService] directly.
 */
class AuraRepository(private val geminiService: GeminiService) {

    private val _outfitAnalysis = MutableStateFlow<OutfitAnalysis?>(null)
    /** Current outfit analysis result, null if not yet analyzed. */
    val outfitAnalysis: StateFlow<OutfitAnalysis?> = _outfitAnalysis.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    /** Full chat history for the current session. */
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    /** True while an AI operation is in progress. */
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    /** Error message from the most recent operation, null if no error. */
    val error: StateFlow<String?> = _error.asStateFlow()

    /** The captured outfit image, kept for reuse in chat context. */
    private var currentOutfitImage: Bitmap? = null

    /**
     * Analyze an outfit image. Updates [outfitAnalysis] and adds
     * an initial greeting message to [chatMessages].
     */
    suspend fun analyzeOutfit(image: Bitmap) {
        _isLoading.value = true
        _error.value = null
        currentOutfitImage = image

        try {
            val analysis = geminiService.analyzeOutfit(image)
            _outfitAnalysis.value = analysis

            // Add the AI's greeting based on analysis
            val greeting = PromptTemplates.outfitGreeting(analysis.summary)
            _chatMessages.value = listOf(
                ChatMessage(
                    role = MessageRole.ASSISTANT,
                    content = greeting
                )
            )
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to analyze outfit"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Send a user message to the AI stylist.
     * Adds both the user message and AI response to [chatMessages].
     */
    suspend fun sendMessage(message: String) {
        val outfitImage = currentOutfitImage ?: return

        // Add user message immediately
        val userMessage = ChatMessage(role = MessageRole.USER, content = message)
        _chatMessages.value = _chatMessages.value + userMessage

        _isLoading.value = true
        _error.value = null

        try {
            val response = geminiService.sendStylistMessage(
                outfitImage = outfitImage,
                chatHistory = _chatMessages.value,
                userMessage = message
            )

            // Add AI response
            val aiMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                content = response.message,
                recommendations = response.recommendations.ifEmpty { null }
            )
            _chatMessages.value = _chatMessages.value + aiMessage

        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to get response"
            // Add error message to chat
            _chatMessages.value = _chatMessages.value + ChatMessage(
                role = MessageRole.ASSISTANT,
                content = "Sorry, I had trouble responding. Please try again."
            )
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Clear the current session (outfit + chat).
     * Call when user wants to analyze a new outfit.
     */
    fun clearSession() {
        _outfitAnalysis.value = null
        _chatMessages.value = emptyList()
        _error.value = null
        currentOutfitImage = null
    }
}

package com.example.aura.data.remote

import android.graphics.Bitmap
import com.example.aura.data.model.ChatMessage
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.data.model.StylistResponse

/**
 * Service interface for all Gemini AI interactions.
 *
 * Implementations should handle:
 * - Image analysis (multimodal vision)
 * - Multi-turn conversations with image context
 * - Response parsing (JSON → data classes)
 *
 * Module B owner: implement this interface in [GeminiServiceImpl].
 */
interface GeminiService {

    /**
     * Analyze an outfit image and return structured clothing detection results.
     *
     * Sends the image with [PromptTemplates.OUTFIT_ANALYSIS_PROMPT] and parses
     * the JSON response into an [OutfitAnalysis].
     *
     * @param image The captured outfit photo (will be resized internally)
     * @return Structured analysis of the outfit
     * @throws Exception if the API call fails or response can't be parsed
     */
    suspend fun analyzeOutfit(image: Bitmap): OutfitAnalysis

    /**
     * Send a message in the ongoing stylist conversation.
     *
     * The outfit image is included as context so the model always knows
     * what the user is wearing. Chat history enables multi-turn dialogue.
     *
     * @param outfitImage The original outfit photo for visual context
     * @param chatHistory Previous messages in the conversation
     * @param userMessage The latest message from the user
     * @return AI stylist response with optional recommendations
     */
    suspend fun sendStylistMessage(
        outfitImage: Bitmap,
        chatHistory: List<ChatMessage>,
        userMessage: String
    ): StylistResponse
}

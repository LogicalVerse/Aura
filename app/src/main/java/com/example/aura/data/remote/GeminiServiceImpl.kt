package com.example.aura.data.remote

import android.graphics.Bitmap
import com.example.aura.data.model.ChatMessage
import com.example.aura.data.model.ClothingItem
import com.example.aura.data.model.MessageRole
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.data.model.Recommendation
import com.example.aura.data.model.StylistResponse
import com.example.aura.util.BitmapUtils
import com.example.aura.util.PromptTemplates
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import org.json.JSONArray
import org.json.JSONObject

/**
 * Implementation of [GeminiService] using Google's Generative AI SDK.
 *
 * TODO (Module B owner): Review and refine this implementation.
 * Key areas to customize:
 * - Error handling and retries
 * - Response parsing robustness
 * - Model parameters (temperature, topK, etc.)
 *
 * @param apiKey Your Gemini API key
 */
class GeminiServiceImpl(apiKey: String) : GeminiService {

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        },
        systemInstruction = content {
            text(PromptTemplates.SYSTEM_PROMPT)
        }
    )

    override suspend fun analyzeOutfit(image: Bitmap): OutfitAnalysis {
        val resized = BitmapUtils.prepareForApi(image)

        val response = model.generateContent(
            content {
                image(resized)
                text(PromptTemplates.OUTFIT_ANALYSIS_PROMPT)
            }
        )

        val jsonText = response.text?.trim()
            ?: throw Exception("Empty response from Gemini")

        return parseOutfitAnalysis(jsonText)
    }

    override suspend fun sendStylistMessage(
        outfitImage: Bitmap,
        chatHistory: List<ChatMessage>,
        userMessage: String
    ): StylistResponse {
        val resized = BitmapUtils.prepareForApi(outfitImage)

        // Build the full conversation content
        val chat = model.startChat(
            history = chatHistory.map { msg ->
                content(role = if (msg.role == MessageRole.USER) "user" else "model") {
                    text(msg.content)
                }
            }
        )

        // Send the new message with the outfit image for context
        val response = chat.sendMessage(
            content("user") {
                image(resized)
                text(userMessage)
            }
        )

        val responseText = response.text?.trim()
            ?: throw Exception("Empty response from Gemini")

        return parseStylistResponse(responseText)
    }

    /**
     * Parse a JSON string into an [OutfitAnalysis].
     * Handles cases where Gemini wraps JSON in markdown code fences.
     */
    private fun parseOutfitAnalysis(jsonText: String): OutfitAnalysis {
        // Strip markdown code fences if present
        val cleaned = jsonText
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val json = JSONObject(cleaned)

            val itemsArray = json.getJSONArray("items")
            val items = (0 until itemsArray.length()).map { i ->
                val item = itemsArray.getJSONObject(i)
                ClothingItem(
                    name = item.getString("name"),
                    category = item.optString("category", ""),
                    color = item.optString("color", "")
                )
            }

            val colorsArray = json.optJSONArray("dominantColors")
            val colors = if (colorsArray != null) {
                (0 until colorsArray.length()).map { colorsArray.getString(it) }
            } else emptyList()

            OutfitAnalysis(
                items = items,
                overallStyle = json.optString("overallStyle", "casual"),
                dominantColors = colors,
                summary = json.optString("summary", "Outfit analyzed successfully")
            )
        } catch (e: Exception) {
            // Fallback: if JSON parsing fails, create a basic analysis
            OutfitAnalysis(
                items = emptyList(),
                overallStyle = "casual",
                dominantColors = emptyList(),
                summary = cleaned.take(200) // Use raw text as summary
            )
        }
    }

    /**
     * Parse a stylist response, extracting recommendations if present.
     * Recommendations are expected within <recommendations> XML tags.
     */
    private fun parseStylistResponse(responseText: String): StylistResponse {
        val recsRegex = Regex("<recommendations>(.*?)</recommendations>", RegexOption.DOT_MATCHES_ALL)
        val match = recsRegex.find(responseText)

        val recommendations = if (match != null) {
            try {
                val recsJson = JSONArray(match.groupValues[1].trim())
                (0 until recsJson.length()).map { i ->
                    val rec = recsJson.getJSONObject(i)
                    Recommendation(
                        itemName = rec.getString("itemName"),
                        description = rec.optString("description", ""),
                        imageUrl = rec.optString("imageUrl", null),
                        category = rec.optString("category", "")
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        // Clean the message text by removing the recommendations block
        val cleanMessage = responseText
            .replace(recsRegex, "")
            .trim()

        return StylistResponse(
            message = cleanMessage,
            recommendations = recommendations
        )
    }
}

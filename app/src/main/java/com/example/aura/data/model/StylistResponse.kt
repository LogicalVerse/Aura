package com.example.aura.data.model

/**
 * Structured response from the AI stylist.
 *
 * The Gemini model returns both a conversational message and optionally
 * a list of product recommendations. This class wraps both.
 *
 * @property message The conversational text reply
 * @property recommendations Optional product suggestions (empty list if none)
 */
data class StylistResponse(
    val message: String,
    val recommendations: List<Recommendation> = emptyList()
)

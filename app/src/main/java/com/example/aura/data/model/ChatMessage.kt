package com.example.aura.data.model

/**
 * A single message in the stylist conversation.
 *
 * @property role Who sent the message (USER or ASSISTANT)
 * @property content The text content of the message
 * @property recommendations Optional list of product recommendations attached to this message
 * @property timestamp Unix millis when the message was created
 */
data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val recommendations: List<Recommendation>? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Identifies the sender of a [ChatMessage].
 */
enum class MessageRole {
    /** Message from the user */
    USER,
    /** Message from the Aura AI stylist */
    ASSISTANT
}

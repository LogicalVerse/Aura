package com.example.aura.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aura.data.model.ChatMessage
import com.example.aura.data.repository.AuraRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Chat/Stylist screen.
 *
 * Manages the conversation between the user and the AI stylist.
 * All state flows through [AuraRepository].
 */
class ChatViewModel(
    private val repository: AuraRepository
) : ViewModel() {

    /** All messages in the current conversation. */
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages

    /** True while waiting for an AI response. */
    val isLoading: StateFlow<Boolean> = repository.isLoading

    /** Error message from the last operation. */
    val error: StateFlow<String?> = repository.error

    /**
     * Send a message to the AI stylist.
     * The user message is added immediately; the AI response
     * arrives asynchronously via [chatMessages].
     */
    fun sendMessage(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(message.trim())
        }
    }
}

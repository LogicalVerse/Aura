package com.example.aura.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aura.data.model.MessageRole
import com.example.aura.ui.chat.components.ChatInput
import com.example.aura.ui.chat.components.MessageBubble
import com.example.aura.ui.chat.components.RecommendationCard
import com.example.aura.ui.components.AuraTopBar

/**
 * Main chat screen for the AI stylist conversation.
 *
 * Module C owner: This is a working scaffold. Customize:
 * - Message animations (fade in, slide up)
 * - Typing indicator while AI is loading
 * - Voice input integration
 * - Chat header with outfit summary
 *
 * @param viewModel ChatViewModel instance
 * @param onBack Navigate back to analysis screen
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        AuraTopBar(
            title = "Aura Stylist",
            onBackClick = onBack
        )

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            reverseLayout = false
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    isUser = message.role == MessageRole.USER
                )

                // Show recommendation cards if present
                message.recommendations?.let { recs ->
                    if (recs.isNotEmpty()) {
                        RecommendationCard(recommendations = recs)
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                item {
                    MessageBubble(
                        message = com.example.aura.data.model.ChatMessage(
                            role = MessageRole.ASSISTANT,
                            content = "Thinking..."
                        ),
                        isUser = false,
                        isLoading = true
                    )
                }
            }
        }

        // Input Bar
        ChatInput(
            onSendMessage = { viewModel.sendMessage(it) },
            isLoading = isLoading
        )
    }
}

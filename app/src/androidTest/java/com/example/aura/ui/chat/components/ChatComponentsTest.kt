package com.example.aura.ui.chat.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.aura.data.model.ChatMessage
import com.example.aura.data.model.MessageRole
import com.example.aura.data.model.Recommendation
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class ChatComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun messageBubble_displaysUserText() {
        val message = ChatMessage(role = MessageRole.USER, content = "Does this shirt match?")
        
        composeTestRule.setContent {
            MessageBubble(message = message, isUser = true)
        }

        composeTestRule.onNodeWithText("Does this shirt match?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Just now").assertIsDisplayed()
    }

    @Test
    fun messageBubble_displaysAuraTextAndAvatar() {
        val message = ChatMessage(role = MessageRole.ASSISTANT, content = "Yes, it looks great!")
        
        composeTestRule.setContent {
            MessageBubble(message = message, isUser = false)
        }

        composeTestRule.onNodeWithText("Yes, it looks great!").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Aura").assertIsDisplayed()
    }

    @Test
    fun suggestionChips_displayAndClick() {
        val suggestions = listOf("Option A", "Option B")
        var clickedSuggestion = ""

        composeTestRule.setContent {
            SuggestionChips(
                suggestions = suggestions,
                visible = true,
                onSuggestionClick = { clickedSuggestion = it }
            )
        }

        composeTestRule.onNodeWithText("Option A").assertIsDisplayed().performClick()
        assertTrue(clickedSuggestion == "Option A")
        
        composeTestRule.onNodeWithText("Option B").assertIsDisplayed().performClick()
        assertTrue(clickedSuggestion == "Option B")
    }

    @Test
    fun recommendationCard_displaysItemDetails() {
        val recs = listOf(
            Recommendation(
                itemName = "Gucci Marmont",
                description = "Classic leather bag",
                category = "bags",
                imageUrl = null // Null URL shows the icon placeholder
            )
        )

        composeTestRule.setContent {
            RecommendationCard(recommendations = recs)
        }

        composeTestRule.onNodeWithText("Gucci Marmont").assertIsDisplayed()
        composeTestRule.onNodeWithText("Classic leather bag").assertIsDisplayed()
        composeTestRule.onNodeWithText("BAGS").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("bags").assertIsDisplayed()
    }

    @Test
    fun typingIndicator_displaysDotsAndAvatar() {
        composeTestRule.setContent {
            TypingIndicator()
        }

        composeTestRule.onNodeWithContentDescription("Aura").assertIsDisplayed()
    }
}

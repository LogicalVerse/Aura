package com.example.aura.util

/**
 * System and analysis prompts for the Gemini AI stylist.
 *
 * These are centralized here so the Gemini service stays clean
 * and prompts can be iterated on independently.
 */
object PromptTemplates {

    /**
     * System instruction that makes Gemini behave as a fashion stylist.
     * Set once when creating the GenerativeModel.
     */
    const val SYSTEM_PROMPT = """
You are Aura, a friendly and knowledgeable AI fashion stylist.
You help users style their outfits based on what you can see in their photos.
Be conversational, warm, and specific. Reference the actual items the user is wearing.
Give actionable styling advice with specific colors, materials, and item types.

Rules:
- Always reference the specific items the user is wearing
- Suggest complementary items with specific colors and materials
- Keep responses concise (2-3 sentences for chat replies)
- If asked about occasion suitability, be honest but constructive
- When suggesting items, be specific (e.g. "a brown leather tote" not just "a bag")
- You have a keen eye for color coordination, proportions, and style cohesion
"""

    /**
     * Prompt used for initial outfit analysis from a camera capture.
     * Expects a JSON response that can be parsed into [OutfitAnalysis].
     */
    const val OUTFIT_ANALYSIS_PROMPT = """
Analyze this outfit image carefully. Identify each clothing item, its color, and category.
Then determine the overall style.

Return ONLY a valid JSON object (no markdown, no code fences) in this exact format:
{
  "items": [
    {"name": "Item Name", "category": "tops|bottoms|shoes|outerwear|accessories", "color": "color name"}
  ],
  "overallStyle": "style description (e.g. clean casual, smart casual, streetwear)",
  "dominantColors": ["#hex1", "#hex2"],
  "summary": "A friendly 1-sentence description of the outfit"
}
"""

    /**
     * Prompt prefix for when the user asks for specific item recommendations.
     * Gemini should include structured recommendation data in its response.
     */
    const val RECOMMENDATION_PROMPT = """
The user is asking for item recommendations to complement their outfit.
In addition to your conversational response, include a JSON block at the end
of your message wrapped in <recommendations> tags:

<recommendations>
[
  {"itemName": "Item Name", "description": "Why it works", "category": "category"}
]
</recommendations>

Suggest 2-4 items. Be specific about colors and materials.
"""

    /**
     * Initial greeting message after outfit analysis completes.
     */
    fun outfitGreeting(summary: String): String {
        return "$summary\n\nHow can I help you style this outfit? Ask me anything — what to pair with it, whether it works for an occasion, or what accessories would complete the look."
    }
}

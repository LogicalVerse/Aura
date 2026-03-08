package com.example.aura.data.model

/**
 * Result of Gemini analyzing an outfit image.
 *
 * @property items Individual clothing items detected in the image
 * @property overallStyle High-level style label (e.g. "clean casual", "smart casual")
 * @property dominantColors Hex color strings for the dominant outfit colors
 * @property summary A friendly 1–2 sentence description of the outfit
 */
data class OutfitAnalysis(
    val items: List<ClothingItem>,
    val overallStyle: String,
    val dominantColors: List<String>,
    val summary: String
)

/**
 * A single clothing item detected in the outfit.
 *
 * @property name Display name (e.g. "Black Jeans")
 * @property category Category bucket: tops, bottoms, shoes, accessories, outerwear
 * @property color Primary color of the item
 */
data class ClothingItem(
    val name: String,
    val category: String,
    val color: String
)

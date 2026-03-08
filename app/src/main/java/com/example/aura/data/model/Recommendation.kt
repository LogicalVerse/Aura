package com.example.aura.data.model

/**
 * A product / item recommendation from the AI stylist.
 *
 * @property itemName Display name (e.g. "Brown Leather Tote")
 * @property description Short reason why it complements the outfit
 * @property imageUrl Optional URL for a product image (null → use placeholder)
 * @property category Item category (bags, shoes, outerwear, accessories, etc.)
 */
data class Recommendation(
    val itemName: String,
    val description: String,
    val imageUrl: String? = null,
    val category: String = ""
)

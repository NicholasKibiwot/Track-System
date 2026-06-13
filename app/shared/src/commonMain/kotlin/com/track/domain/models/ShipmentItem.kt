package com.track.domain.models

import kotlinx.serialization.Serializable

/**
 * Categorizes the printing machines based on their size and printing capacity.
 * Using an Enum prevents typos and makes it easy to filter later (e.g., "Show me only BILLBOARD orders").
 */
@Serializable
enum class MachineType(
    val displayName: String,
) {
    SMALL("Small Desktop"),
    MEDIUM("Office/Commercial"),
    LARGE("Large Format"),
    BILLBOARD("Billboard / Industrial"),
    ;

    override fun toString(): String = displayName
}

/**
 * Represents a specific printing machine being transported.
 * This describes the "Goods" in "Goods on Transit".
 */
data class ShipmentItem(
    val id: String, // Unique Serial Number or SKU
    val model: String, // e.g., "HP Latex 800", "Heidelberg XL"
    val machineType: MachineType, // Size category for logistics planning
    val weight: Double, // In Kilograms
    val length: Double, // In Centimeters
    val width: Double, // In Centimeters
    val height: Double, // In Centimeters
    val isFragile: Boolean = false, // Special handling required?
    val imageUrl: String? = null, // URL for an image of the machine (optional)
)


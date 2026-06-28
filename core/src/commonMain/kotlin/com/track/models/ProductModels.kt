package com.track.models

import kotlinx.serialization.Serializable

@Serializable
enum class ProductCategory(val displayName: String) {
    OFFICE_PRINTERS("Office Printers"),
    POS_RETAIL_PRINTERS("POS & Retail Printers"),
    COMMERCIAL_GRAPHICS_PRINTERS("Commercial & Graphics Printers"),
    INDUSTRIAL_SPECIALTY_PRINTERS("Industrial & Specialty Printers"),
    ACCESSORIES("Accessories"),
    REPAIRS_SERVICES("Repairs & Services")
}

@Serializable
enum class PrinterType(val displayName: String) {
    LASER("Laser"),
    INKJET("Inkjet"),
    MULTIFUNCTION("Multifunction"),
    THERMAL("Thermal"),
    BARCODE("Barcode"),
    LARGE_FORMAT("Large Format"),
    UV("UV"),
    DTF("DTF"),
    SUBLIMATION("Sublimation"),
    THREE_D("3D"),
    DOT_MATRIX("Dot Matrix"),
    CARD_PRINTER("Card Printer")
}

@Serializable
data class ImageStorage(
    val originalPath: String = "",
    val webpPath: String = "",
    val thumbPath: String = "",
    val webpUrl: String = "", // Added for gs:// or https:// urls
    val thumbnailUrl: String = ""
)

@Serializable
data class ProductImage(
    val id: String = "",
    val entityType: String = "PRODUCT",
    val entityId: String = "",
    val category: String = "",
    val type: String = "PRIMARY",
    val alt: String = "",
    val sourceType: String = "",
    val sourceName: String = "",
    val sourcePage: String? = null,
    val storage: ImageStorage = ImageStorage(),
    val status: String = "APPROVED",
    val createdAt: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val licenseStatus: String = ""
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val printerTypes: List<PrinterType> = emptyList()
)

@Serializable
enum class MachineType(val displayName: String) {
    SMALL("Small Desktop"),
    MEDIUM("Office/Commercial"),
    LARGE("Large Format"),
    BILLBOARD("Billboard / Industrial")
}

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val category: ProductCategory = ProductCategory.OFFICE_PRINTERS,
    val printerType: PrinterType? = null,
    val brand: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "", // Legacy support
    val currency: String = "KES",
    val stockStatus: String = "IN_STOCK",
    val stock: Int = 0, // Keep for backward compatibility
    val images: List<ProductImage> = emptyList(),
    val compatibleModels: List<String> = emptyList(),
    val compatiblePrinterTypes: List<PrinterType> = emptyList(),
    val rating: Double = 0.0,
    val isActive: Boolean = true,
    val addedBy: String = "",
    val branch: String = "",
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList()
)

@Serializable
data class RepairService(
    val id: String = "",
    val name: String = "",
    val category: String = "REPAIR",
    val printerType: PrinterType? = null,
    val description: String = "",
    val deliveryMode: List<String> = emptyList(),
    val inspectionFee: Double = 0.0,
    val currency: String = "KES",
    val trackingEnabled: Boolean = true,
    val imageSourceType: String = "OWN_OR_LICENSED"
)

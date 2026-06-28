package com.track.data

import com.track.models.Product
import com.track.models.ProductCategory
import com.track.models.PrinterType
import com.track.models.ProductImage
import com.track.models.ImageStorage

object SeedData {
    val products = listOf(
        Product(
            id = "prd_hp_ljp_4103",
            name = "HP LaserJet Pro 4103fdw",
            category = ProductCategory.OFFICE_PRINTERS,
            printerType = PrinterType.MULTIFUNCTION,
            brand = "HP",
            description = "Built for maximum productivity with fast speeds and reliable hardware, delivering effortless everyday use from wherever work happens so you can focus more on your business.",
            price = 65000.0,
            currency = "KES",
            stockStatus = "IN_STOCK",
            images = listOf(
                ProductImage(
                    id = "img_hp_4103_main",
                    entityId = "prd_hp_ljp_4103",
                    category = "OFFICE_PRINTERS",
                    alt = "HP LaserJet Pro 4103fdw Multifunction Printer",
                    sourceType = "MANUFACTURER",
                    sourceName = "HP",
                    storage = ImageStorage(
                        originalPath = "product-images/printers/office/hp-4103.jpg",
                        webpPath = "product-images/printers/office/hp-4103.webp",
                        thumbPath = "product-images/printers/office/thumb/hp-4103.webp"
                    )
                )
            )
        ),
        Product(
            id = "prd_epson_l3250",
            name = "Epson EcoTank L3250 Wi-Fi All-in-One Ink Tank Printer",
            category = ProductCategory.OFFICE_PRINTERS,
            printerType = PrinterType.INKJET,
            brand = "Epson",
            description = "Save up to 90% on printing costs with Epson’s cartridge-free EcoTank printers. Supplied with high yield ink bottles, the integrated ink tanks are easy to fill thanks to the specially engineered ink bottles.",
            price = 32000.0,
            currency = "KES",
            stockStatus = "IN_STOCK",
            images = listOf(
                ProductImage(
                    id = "img_epson_l3250_main",
                    entityId = "prd_epson_l3250",
                    category = "OFFICE_PRINTERS",
                    alt = "Epson EcoTank L3250 Ink Tank Printer",
                    sourceType = "MANUFACTURER",
                    sourceName = "Epson",
                    storage = ImageStorage(
                        originalPath = "product-images/printers/inkjet/epson-l3250.jpg",
                        webpPath = "product-images/printers/inkjet/epson-l3250.webp",
                        thumbPath = "product-images/printers/inkjet/thumb/epson-l3250.webp"
                    )
                )
            )
        ),
        Product(
            id = "prd_bixolon_srp350",
            name = "Bixolon SRP-350III 3-inch Thermal Receipt Printer",
            category = ProductCategory.POS_RETAIL_PRINTERS,
            printerType = PrinterType.THERMAL,
            brand = "Bixolon",
            description = "Providing high quality printing performance at speed of up to 250mm/sec. The SRP-350III is ideal for retail, hospitality, ticketing and more.",
            price = 18500.0,
            currency = "KES",
            stockStatus = "IN_STOCK",
            images = listOf(
                ProductImage(
                    id = "img_bixolon_srp350_main",
                    entityId = "prd_bixolon_srp350",
                    category = "POS_RETAIL_PRINTERS",
                    alt = "Bixolon SRP-350III Thermal Receipt Printer",
                    sourceType = "MANUFACTURER",
                    sourceName = "Bixolon",
                    storage = ImageStorage(
                        originalPath = "product-images/printers/thermal/bixolon-srp350.jpg",
                        webpPath = "product-images/printers/thermal/bixolon-srp350.webp",
                        thumbPath = "product-images/printers/thermal/thumb/bixolon-srp350.webp"
                    )
                )
            )
        ),
        Product(
            id = "prd_mimaki_ujv200",
            name = "Mimaki UJV200-136 UV-LED Roll-to-Roll Printer",
            category = ProductCategory.INDUSTRIAL_SPECIALTY_PRINTERS,
            printerType = PrinterType.UV,
            brand = "Mimaki",
            description = "High-quality, cost-effective UV-LED roll-to-roll printer with 1.3m width. Features advanced Mimaki technologies for stable and high-quality production.",
            price = 1250000.0,
            currency = "KES",
            stockStatus = "IN_STOCK",
            images = listOf(
                ProductImage(
                    id = "img_mimaki_ujv200_main",
                    entityId = "prd_mimaki_ujv200",
                    category = "INDUSTRIAL_SPECIALTY_PRINTERS",
                    alt = "Mimaki UJV200 Series UV LED roll to roll printer",
                    sourceType = "MANUFACTURER",
                    sourceName = "Mimaki USA",
                    storage = ImageStorage(
                        originalPath = "product-images/printers/uv/mimaki-ujv200.jpg",
                        webpPath = "product-images/printers/uv/mimaki-ujv200.webp",
                        thumbPath = "product-images/printers/uv/thumb/mimaki-ujv200.webp"
                    )
                )
            )
        ),
        Product(
            id = "acc_toner_hp_4103_black",
            name = "Original Black Toner for HP LaserJet Pro 4103 Series",
            category = ProductCategory.ACCESSORIES,
            brand = "HP",
            description = "High-yield original black toner designed for sharp text, stable page output, and reliable office printing.",
            price = 14500.0,
            currency = "KES",
            stockStatus = "IN_STOCK",
            compatibleModels = listOf("HP LaserJet Pro 4103fdw"),
            images = listOf(
                ProductImage(
                    id = "img_hp_4103_toner_black",
                    entityId = "acc_toner_hp_4103_black",
                    category = "ACCESSORIES",
                    alt = "HP 4103 Black Toner Cartridge",
                    sourceType = "MANUFACTURER",
                    sourceName = "HP",
                    storage = ImageStorage(
                        originalPath = "product-images/accessories/toner/hp-4103-black.jpg",
                        webpPath = "product-images/accessories/toner/hp-4103-black.webp",
                        thumbPath = "product-images/accessories/toner/thumb/hp-4103-black.webp"
                    )
                )
            )
        ),
        Product(
            id = "ser_printer_maint_standard",
            name = "Standard Printer Maintenance Service",
            category = ProductCategory.REPAIRS_SERVICES,
            description = "Comprehensive cleaning, calibration and component health check for office printers.",
            price = 3500.0,
            currency = "KES",
            stockStatus = "IN_STOCK",
            isActive = true
        )
    )
}

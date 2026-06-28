package com.track.models

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val id: String = "",
    val orderId: String = "",
    val customerId: String = "",
    val amount: Double = 0.0,
    val currency: String = "KES",
    val method: PaymentMethod = PaymentMethod.M_PESA,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val transactionId: String? = null, // External ID from M-Pesa/Bank
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable
data class Invoice(
    val id: String = "",
    val orderId: String = "",
    val invoiceNumber: String = "",
    val amount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "DRAFT", // DRAFT, SENT, PAID, VOID
    val dueDate: Long = 0L,
    val createdAt: Long = 0L
)

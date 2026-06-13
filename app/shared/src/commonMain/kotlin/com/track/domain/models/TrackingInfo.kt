package com.track.domain.models


data class TrackingInfo(
    val id: String = "",
    val orderId: String = "",
    val driverId: String = "",
    val currentLocation: GeoLocation? = null,
    val status: OrderStatus = OrderStatus.PENDING,
    val lastUpdated: Long = 0L,
)


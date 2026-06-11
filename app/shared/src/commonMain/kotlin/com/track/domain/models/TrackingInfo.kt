package com.track.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class TrackingInfo(
    @DocumentId val id: String = "",
    val orderId: String = "",
    val driverId: String = "",
    val currentLocation: GeoLocation? = null,
    val status: OrderStatus = OrderStatus.PENDING,
    val lastUpdated: Timestamp = Timestamp.now(),
)

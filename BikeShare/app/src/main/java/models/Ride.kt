package models

import java.util.*

data class Ride(
    var id: UUID,
    var bike: Bike? = null,
    var user: User? = null,
    var startLocation: String = "",
    var endLocation: String = "",
    var startTime: Date = Date(),
    var endTime: Date = Date(),
    var price: Double = 0.0,
    var hasEnd: Boolean = false)
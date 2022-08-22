package models

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import java.util.*

data class Bike(
    var id: UUID,
    var name: String = "",
    var price: Int = 10,
    var picture: Bitmap,
    var inUse: Boolean = false,
    var longitude: Double = 0.0,
    var latitude: Double = 0.0
)
package models

import java.util.*

open class User(
    var id: UUID,
    var name: String,
    var password: String,
    var balance: Double = 0.0
)
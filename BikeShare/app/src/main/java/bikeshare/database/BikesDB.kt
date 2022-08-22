package bikeshare.database

import android.content.Context
import android.graphics.BitmapFactory
import bikeshare.R
import models.Bike
import models.Ride
import models.User
import java.util.*

class BikesDB private constructor(context: Context){
    private var bikes = arrayOf<Bike>()

    init {
        bikes += Bike(id = UUID.randomUUID(), name = "Cargo bike", price = 25, picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.ladcykel),false,12.546275, 55.683488)
        bikes += Bike(id = UUID.randomUUID(), name = "Racing bike", price = 45, picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.racercykel), false,12.590431,55.679630)
        bikes += Bike(id = UUID.randomUUID(), name = "Bike", price = 10, picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.herrecykel), false,12.568570,55.675524)

    }

    companion object : BikesDBHolder<BikesDB, Context>(::BikesDB)

    fun getBikesDB() : Array<Bike> {
        return bikes
    }

    fun addBike(newBike: Bike){
        bikes += newBike
    }

    fun updateBike(updatedBike: Bike){
        bikes.set(bikes.indexOf(updatedBike),updatedBike)
    }
}

open class BikesDBHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun get(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null)
            return checkInstance

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null)
                checkInstanceAgain

            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}
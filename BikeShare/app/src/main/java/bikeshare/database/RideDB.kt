package bikeshare.database

import android.content.Context
import models.Ride
import models.User
import java.util.*

class RidesDB private constructor(context: Context) {
    private var rides = arrayOf<Ride>()

    init {
    }

    companion object : RidesDBHolder<RidesDB, Context>(::RidesDB)

    fun getRidesDB() : Array<Ride> {
        return rides
    }

    fun deleteRide(ride: Ride) {
        val index = rides.indexOf(ride)
        val arrList = rides.toMutableList()
        arrList.removeAt(index)
        rides = arrList.toTypedArray()
    }

    fun addRide(newRide: Ride){
        rides += newRide
    }

    fun updateRide(updatedRide: Ride){
        rides.set(rides.indexOf(updatedRide), updatedRide)
    }
}

open class RidesDBHolder<out T: Any, in A>(creator: (A) -> T) {
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
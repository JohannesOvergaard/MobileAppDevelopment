package bikeshare.database

import android.content.Context
import models.User
import java.util.*

class UsersDB private constructor(context: Context) {
    private var users = arrayOf<User>()

    init {
        users += User(id = UUID.randomUUID(), name = "John", password = "1234", balance = 100.00)
        users += User(id = UUID.randomUUID(), name = "Emma", password = "1234", balance = 50.00)
    }

    companion object : UsersDBHolder<UsersDB, Context>(::UsersDB)

    fun getUsersDB() : Array<User> {
        return users
    }

    fun getUser(name:String?) :User? {
        for (user in users) {
            if (user.name == name)
                return user
        }
        return null
    }

    fun updateUser(updatedUser: User) {
        users.set(users.indexOf(updatedUser),updatedUser)
    }

    fun addUser(newUser: User){
        users += newUser
    }
}

open class UsersDBHolder<out T: Any, in A>(creator: (A) -> T) {
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
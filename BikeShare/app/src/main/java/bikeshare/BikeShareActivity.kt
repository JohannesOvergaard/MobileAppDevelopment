package bikeshare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import bikeshare.database.BikesDB
import bikeshare.database.RidesDB
import bikeshare.database.UsersDB

class BikeShareActivity : AppCompatActivity() {
    companion object {
        lateinit var sUsersDB: UsersDB
        lateinit var sBikesDB: BikesDB
        lateinit var sRidesDB: RidesDB
    }

    private lateinit var mPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPreferences = getPreferences(Context.MODE_PRIVATE)!!

        // Singleton to share an object between the app activities and fragments
        sBikesDB = BikesDB.get(this)
        sRidesDB = RidesDB.get(this)
        sUsersDB = UsersDB.get(this)

        setContentView(R.layout.activity_bike_share)

        val fragment = supportFragmentManager
            .findFragmentById(R.id.fragment_bike_share)

        if (fragment == null) {
            if (retrievePreferences() == null) {
                changeBikeShareFragment(LoginFragment())
            } else {
                changeBikeShareFragment(BikeShareFragment())
            }
        }
    }

    fun selectFragment(view: View) {
        val fragment = when (view.id) {
            R.id.start_button -> StartRideFragment()
            else -> EndRideFragment()
        }

        changeDisplayFragment(fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.bikeshare_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_history -> changeDisplayFragment(ListRideFragment())
            R.id.menu_item_add_bike -> {
                startActivity(Intent(this,AddBikeActivity::class.java))
            }
            R.id.menu_item_user -> {
                changeDisplayFragment(UserFragment())
            }
            R.id.menu_item_logout -> {
                setPreferencesToDef()
                changeBikeShareFragment(LoginFragment())
            }
            else -> return false
        }

        return true
    }

    private fun changeBikeShareFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_bike_share, fragment)
            .setTransition(
                FragmentTransaction
                    .TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun changeDisplayFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_display, fragment)
            .setTransition(
                FragmentTransaction
                    .TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun setPreferencesToDef(){
        val editor = mPreferences.edit()
        editor
            .putString("USERNAME", null)
            .apply()
    }

    private fun retrievePreferences(): String? {
        return mPreferences.getString("USERNAME", null)
    }
}
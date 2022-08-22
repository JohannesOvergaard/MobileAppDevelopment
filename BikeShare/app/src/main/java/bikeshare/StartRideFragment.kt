package bikeshare

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bikeshare.database.BikesDB
import bikeshare.database.RidesDB
import bikeshare.database.UsersDB
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import models.Bike
import models.Ride
import models.User
import java.util.*

class StartRideFragment : Fragment() {

    private lateinit var mLocationManager: LocationManager
    private lateinit var mPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recyclerview, container, false)

        mLocationManager = LocationManager(context!!)
        mPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create the adapter
        val results = BikeShareActivity.sBikesDB.getBikesDB().filter { bike -> !bike.inUse }
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = BikeAdapter(results)

        title.text = "Click on the bike you want to ride"
    }

    private inner class BikeAdapter(var mbikes: List<Bike>) : RecyclerView.Adapter<BikeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
            val layout = layoutInflater
                .inflate(R.layout.list_item_bike, parent, false)
            return BikeViewHolder(layout)
        }

        override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
            val bike = mbikes[position]
            holder.bind(bike)
        }

        override fun getItemCount(): Int {
            return BikeShareActivity.sBikesDB.getBikesDB()
                .filter { bike ->!bike.inUse  }
                .size
        }
    }

    private inner class BikeViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val mBikeName: TextView = viewItem.findViewById(R.id.bike_name)
        val mBikePicture: ImageView = viewItem.findViewById(R.id.bike_picture)
        val mBikePrice: TextView = viewItem.findViewById(R.id.bike_price)
        val mLocation: TextView = viewItem.findViewById(R.id.bike_location)

        fun bind(bike: Bike)
        {
            mBikeName.text = bike.name
            mBikePicture.setImageBitmap(bike.picture)
            mBikePrice.text = "${bike.price} money per hour"
            val bikeLocation = mLocationManager.getAddress(bike.longitude,bike.latitude)
            mLocation.text = bikeLocation

            itemView.setOnClickListener {
                // Alert dialog.
                val builder = AlertDialog.Builder(activity).apply {

                    setTitle("Do you want to start a ride on ${bike.name}?")
                    setPositiveButton(android.R.string.yes, null)
                    setNegativeButton(android.R.string.no, null)
                }

                builder.setPositiveButton("YES") { dialog, _ ->
                    val userName = mPreferences.getString("USERNAME", null)
                    val dbUser = BikeShareActivity.sUsersDB.getUser(userName)

                    val ride = Ride(UUID.randomUUID(),bike,dbUser)
                    ride.startLocation = bikeLocation
                    ride.startTime = Date()
                    BikeShareActivity.sRidesDB.addRide(ride)

                    bike.inUse = true
                    BikeShareActivity.sBikesDB.updateBike(bike)

                    fragmentManager!!
                        .beginTransaction()
                        .replace(R.id.fragment_bike_share, BikeShareFragment())
                        .setTransition(
                            FragmentTransaction
                                .TRANSIT_FRAGMENT_FADE)
                        .commit()

                    dialog.dismiss()
                }

                builder.setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
        }
    }
}

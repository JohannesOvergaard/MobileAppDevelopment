package bikeshare

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bikeshare.database.BikesDB
import bikeshare.database.RidesDB
import bikeshare.database.UsersDB
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import models.Ride
import java.text.SimpleDateFormat
import java.util.*

class EndRideFragment : Fragment() {

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

        val userName = mPreferences.getString("USERNAME", null)

        // Create the adapter
        val results = BikeShareActivity.sRidesDB.getRidesDB().filter { ride -> !ride.hasEnd }.filter { ride -> ride.user?.name == userName }
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = RideAdapter(results)

        title.text = "Click on the ride you want to end"
    }

    private inner class RideAdapter(var mRides: List<Ride>) : RecyclerView.Adapter<RideViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
            val layout = layoutInflater
                .inflate(R.layout.list_item_ride, parent, false)
            return RideViewHolder(layout)
        }

        override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
            val ride = mRides[position]
            holder.bind(ride!!, this)
        }

        override fun getItemCount(): Int {
            return BikeShareActivity.sRidesDB.getRidesDB()
                .filter { ride -> !ride.hasEnd }
                .size
        }
    }

    private inner class RideViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val mBikeName: TextView = viewItem.findViewById(R.id.bike_name)
        val mBikePicture: ImageView = viewItem.findViewById(R.id.bike_picture)
        val mRideInformation: TextView = viewItem.findViewById(R.id.ride_information)

        fun bind(ride: Ride, rideAdapter: RideAdapter)
        {
            mBikeName.text = ride.bike?.name
            mBikePicture.setImageBitmap(ride.bike?.picture)
            val price = "%.2f".format((Date().time-ride.startTime.time)*ride.bike?.price!!.toDouble()/3600000).toDouble()
            mRideInformation.text = "Your started this ride from ${ride.startLocation} at ${SimpleDateFormat("HH:mm dd/MM/yyyy").format(ride.startTime)}. The current price are ${price} money."

            itemView.setOnClickListener {
                // Alert dialog.

                val builder = AlertDialog.Builder(activity).apply {

                    setTitle("Will you end the ride on ${ride.bike?.name}?")
                    setPositiveButton(android.R.string.yes, null)
                    setNegativeButton(android.R.string.no, null)
                }

                builder.setPositiveButton("YES") { dialog, _ ->
                    ride.bike?.inUse = false
                    ride.bike?.longitude = mLocationManager.mLongitude
                    ride.bike?.latitude = mLocationManager.mLatitude
                    ride.endLocation = mLocationManager.getAddress(mLocationManager.mLongitude,mLocationManager.mLatitude)
                    ride.endTime = Date()
                    ride.hasEnd = true
                    ride.price = price
                    if (ride.user?.balance != null) {
                        ride.user!!.balance -= price
                    }
                    BikeShareActivity.sRidesDB.updateRide(ride)

                    dialog.dismiss()

                    fragmentManager!!
                        .beginTransaction()
                        .replace(R.id.fragment_bike_share, BikeShareFragment())
                        .setTransition(
                            FragmentTransaction
                                .TRANSIT_FRAGMENT_FADE)
                        .commit()
                }

                builder.setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mLocationManager.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mLocationManager.stopLocationUpdates()
    }
}
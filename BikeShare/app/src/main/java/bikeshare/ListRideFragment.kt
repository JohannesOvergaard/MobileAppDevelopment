package bikeshare

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bikeshare.database.RidesDB
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import models.Ride
import java.text.SimpleDateFormat
import java.util.*

class ListRideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recyclerview, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val results = BikeShareActivity.sRidesDB.getRidesDB().filter { ride -> ride.hasEnd }
        recycler_view.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
        recycler_view.adapter = RideAdapter(results)

        title.text = "Here you find all previous rides"
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
                .filter { ride -> ride.hasEnd }
                .size
        }
    }

    private inner class RideViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        val mBikeName: TextView = viewItem.findViewById(R.id.bike_name)
        val mBikePicture: ImageView = viewItem.findViewById(R.id.bike_picture)
        val mBikeInformation: TextView = viewItem.findViewById(R.id.ride_information)

        fun bind(ride: Ride, adapter: RideAdapter)
        {
            mBikeName.text = "${ride.user?.name} rode ${ride.bike?.name}\nDate: ${SimpleDateFormat("HH:mm dd/MM/yyyy").format(ride.startTime)}"
            mBikePicture.setImageBitmap(ride.bike!!.picture)
            val duration = "%.2f".format((ride.endTime.time-ride.startTime.time).toDouble()/60000).toDouble()
            mBikeInformation.text = "From: ${ride.startLocation}\nTo: ${ride.endLocation}\nDuration: $duration minutes.\nThe price was ${ride.price} money."

                itemView.setOnClickListener {
                val builder = AlertDialog.Builder(activity).apply {

                    setTitle("Do you wanna to delete the ride?")
                    setPositiveButton(android.R.string.yes, null)
                    setNegativeButton(android.R.string.no, null)
                }

                builder.setPositiveButton("YES") { dialog, _ ->
                    BikeShareActivity.sRidesDB.deleteRide(ride)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }

                builder.setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
        }
    }
}

package bikeshare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import bikeshare.database.BikesDB
import bikeshare.database.RidesDB
import bikeshare.database.UsersDB
import kotlinx.android.synthetic.main.fragment_bike_share.*
import kotlin.collections.ArrayList


class BikeShareFragment : Fragment() {
    companion object {
        private const val ALL_PERMISSIONS_RESULT = 1011
        const val UPDATE_INTERVAL = 5000L
        const val FASTEST_INTERVAL = 5000L
    }

    private val mPermissions: ArrayList<String> = ArrayList()

    private lateinit var mLocationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bike_share, container, false)

        mLocationManager = LocationManager(context!!)

        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        mPermissions.add(Manifest.permission.READ_CONTACTS)
        mPermissions.add(Manifest.permission.CAMERA)

        val permissionsToRequest = permissionsToRequest(mPermissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (permissionsToRequest.size > 0)
                requestPermissions(
                    permissionsToRequest.toTypedArray(),
                    ALL_PERMISSIONS_RESULT
            )

        checkPermission()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        maps_button.setOnClickListener {
            val intent = Intent(activity, MapActivity::class.java).apply {
                putExtra("longitude", mLocationManager.mLongitude)
                putExtra("latitude", mLocationManager.mLatitude)
            }
            startActivity(intent)
        }
    }

    private fun permissionsToRequest(permissions: java.util.ArrayList<String>): java.util.ArrayList<String> {
        val result: java.util.ArrayList<String> = java.util.ArrayList()
        for (permission in permissions)
            if (!hasPermission(permission))
                result.add(permission)
        return result
    }

    private fun hasPermission(permission: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        else
            true

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            context!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context!!, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bikeshare_menu, menu)
    }

    override fun onResume() {
        super.onResume()
        mLocationManager.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mLocationManager.stopLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

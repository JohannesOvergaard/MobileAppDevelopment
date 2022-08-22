package bikeshare

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*

class LocationManager(context: Context) {

    var mLongitude = 0.0
    var mLatitude = 0.0
    private var mContext = context

    private var mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
    private var mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLongitude = location.longitude
                mLatitude = location.latitude
            }
        }
    }

    fun getAddress(longitude: Double, latitude: Double): String {
        println("Address: $longitude , $latitude")

        val geocoder = Geocoder(mContext, Locale.getDefault())
        val stringBuilder = StringBuilder()

        try {
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                stringBuilder.apply{
                    append(address.getAddressLine(0))
                }
            } else
                return "No address found"

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return stringBuilder.toString()
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            mContext!!, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    mContext!!, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED


    fun startLocationUpdates() {
        if (checkPermission())
            return

        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = BikeShareFragment.UPDATE_INTERVAL
            fastestInterval = BikeShareFragment.FASTEST_INTERVAL
        }

        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest, mLocationCallback, null
        )
    }

    fun stopLocationUpdates() {
        mFusedLocationProviderClient
            .removeLocationUpdates(mLocationCallback)
    }
}
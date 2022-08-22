package bikeshare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap

class MapActivity : AppCompatActivity() {

    private val mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        var fragment = supportFragmentManager
                .findFragmentById(R.id.map)

        if (fragment == null) {
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val markerTitle = intent.getStringExtra("markerTitle")

            val bundle = Bundle().apply {
                putDouble("longitude", longitude)
                putDouble("latitude", latitude)
                putString("markerTitle", markerTitle)
            }

            fragment = MapFragment().apply {
                arguments = bundle
            }

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, fragment)
                .commit()
        }
    }

}

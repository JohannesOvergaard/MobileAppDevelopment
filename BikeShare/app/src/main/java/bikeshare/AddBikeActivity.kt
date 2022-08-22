package bikeshare

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AddBikeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bike)

        var fragment = supportFragmentManager
            .findFragmentById(R.id.addBike)

        if (fragment == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, AddBikeFragment())
                .commit()
        }
    }
}
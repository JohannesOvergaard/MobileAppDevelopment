package bikeshare

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_add_bike.*
import models.Bike
import java.util.*

class AddBikeFragment : Fragment() {

    private var mPrice: Int = 10
    private val requestImageCapture = 1

    private lateinit var pictureFile: Bitmap
    private lateinit var mLocationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_bike, container, false)

        mLocationManager = LocationManager(context!!)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        price.text = "$mPrice money"

        decreasePriceButton.setOnClickListener {
            if (mPrice >= 10) {
                mPrice -= 5
                price.text = "$mPrice money"
            }
        }

        increasePriceButton.setOnClickListener {
            if (mPrice <= 95) {
                mPrice += 5
                price.text = "$mPrice money"
            }
        }
        addPicture.setOnClickListener {


            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, requestImageCapture)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }

        addBike_button.setOnClickListener {
            // Alert dialog.
            val builder = AlertDialog.Builder(activity).apply {
                setTitle("Do you want to add a bike?")
                setPositiveButton(android.R.string.yes, null)
                setNegativeButton(android.R.string.no, null)
            }

            builder.setPositiveButton("YES") { dialog, _ ->
                if (nameInput.text.isNotEmpty() && pictureFile != null) {

                    val bike = Bike(UUID.randomUUID(), nameInput.text.toString(),mPrice,pictureFile,false,mLocationManager.mLongitude,mLocationManager.mLatitude)

                    BikeShareActivity.sBikesDB.addBike(bike)

                    nameInput.text.clear()
                    picture.text = ""

                    Toast.makeText(
                        requireContext(),
                        "The bike has been successfully added",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please give the bike a name and add a picture",
                        Toast.LENGTH_LONG
                    ).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }

            builder.create().show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestImageCapture && resultCode == RESULT_OK) {
            pictureFile = data?.extras?.get("data") as Bitmap
        }
    }
}

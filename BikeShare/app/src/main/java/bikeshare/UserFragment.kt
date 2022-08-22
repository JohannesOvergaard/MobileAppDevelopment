package bikeshare

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import bikeshare.database.UsersDB
import kotlinx.android.synthetic.main.fragment_user.*
import models.User

class UserFragment : Fragment() {

    private lateinit var mPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        mPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = mPreferences.getString("USERNAME", null)
        val dbUser = BikeShareActivity.sUsersDB.getUser(userName)
        val userBalance = dbUser?.balance

        user_name.text = dbUser?.name
        balance.text = "$userBalance money"

        increase_balance_button.setOnClickListener {
            // Alert dialog.
            val builder = AlertDialog.Builder(activity).apply {
                setTitle("Do you want to add 50 money to your BikeShare account?")
                setPositiveButton(android.R.string.yes, null)
                setNegativeButton(android.R.string.no, null)
            }

            builder.setPositiveButton("YES") { dialog, _ ->



                if (dbUser != null) {
                    dbUser.balance = dbUser.balance.plus(50)
                    BikeShareActivity.sUsersDB.updateUser(dbUser)
                }

                fragmentManager!!
                    .beginTransaction()
                    .replace(R.id.fragment_display, UserFragment())
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

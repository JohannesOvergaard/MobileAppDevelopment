package bikeshare

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import bikeshare.database.UsersDB
import kotlinx.android.synthetic.main.fragment_login.*
import models.User
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var mPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        mPreferences = activity?.getPreferences(Context.MODE_PRIVATE)!!

        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_button.setOnClickListener {
            val userName = loginName.text.toString().trim().capitalize()
            val dbUser = BikeShareActivity.sUsersDB.getUser(userName)
            if (dbUser != null) {
                if (dbUser.name == userName && dbUser.password == loginPassword.text.toString()){
                    loginUser(userName)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "The user name or the password is not correct. \nPlease type in a valid user name and password or create a new user.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "The user name or the password is not correct. \n Please type in a valid user name and password or create a new user.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        create_user_button.setOnClickListener {
            // Alert dialog.
            val builder = AlertDialog.Builder(activity).apply {
                setTitle("Do you want to add ${loginName.text} as a new user?")
                setPositiveButton(android.R.string.yes, null)
                setNegativeButton(android.R.string.no, null)
            }

            builder.setPositiveButton("YES") { dialog, _ ->
                val userName = loginName.text.toString().trim().capitalize()
                val dbUser = BikeShareActivity.sUsersDB.getUser(userName)
                //User does not exist yet
                if (dbUser == null) {
                    if (userName.isNotEmpty() && loginPassword.text.isNotEmpty()) {

                        val newUser = User(UUID.randomUUID(), userName, loginPassword.text.toString(), 0.0)
                        BikeShareActivity.sUsersDB.addUser(newUser)

                        loginUser(userName)


                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please type in a user name and a password.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "The user name is already taken. Please type in another name.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                dialog.dismiss()
            }

            builder.setNegativeButton("NO") { dialog, _ -> dialog.dismiss() }

            builder.create().show()
        }
    }

    private fun loginUser(name: String) {
        fragmentManager!!
            .beginTransaction()
            .replace(R.id.fragment_bike_share, BikeShareFragment())
            .setTransition(
                FragmentTransaction
                    .TRANSIT_FRAGMENT_FADE
            )
            .commit()

        savePreferences(name)
    }

    fun savePreferences(userName: String) {
        val editor = mPreferences.edit()
        editor
            .putString("USERNAME", userName)
            .apply()
    }
}
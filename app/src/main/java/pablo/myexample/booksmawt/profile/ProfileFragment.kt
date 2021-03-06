package pablo.myexample.booksmawt.profile

import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.*

import pablo.myexample.booksmawt.databinding.ProfileFragmentBinding
import java.lang.Exception
import kotlin.math.sign

class ProfileFragment : Fragment() {

    private lateinit var url: String
    private lateinit var userId: String
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var model: Communicator
    private var uploadedBook: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity!!.bottom_nav_view.visibility == View.INVISIBLE) {
            activity!!.bottom_nav_view.visibility = View.VISIBLE
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        loadData()
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        binding.apply {
            editProfileButton.setOnClickListener {
                toEditProfile()
            }
            signOutButton.setOnClickListener {
                Logout()
            }
        }
        checkForBookUploads()
    }

    private fun checkForBookUploads(){
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Cities").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when {
                    snapshot.exists() -> {
                       uploadedBook = true
                    }
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    private fun okDialog() {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        dialogBuilder.setMessage("Cannot edit profile when book uploads have been made.")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss()}
        val alert = dialogBuilder.create()
        alert.setTitle("Sorry")
        alert.show()
    }

    private fun toEditProfile() {
        when (uploadedBook) {
            false -> {
                val profile = Profile(
                    binding.profileName.text.toString(),
                    binding.profileLocation.text.toString(),
                    url
                )
                model.passProfileObj(profile as Profile)
                activity!!.bottom_nav_view.visibility = View.INVISIBLE
                view!!.findNavController().navigate(R.id.action_navigation_profile_to_editProfile)
            }
            else -> {
                okDialog()
            }
        }
    }

    private fun Logout() {
        //ondestroy service method is called after calling stopservice
        val intent = Intent(activity!!, Service::class.java)
        activity!!.stopService(intent)
        //clear token
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Token")
            .setValue("N/A")
        //logout of firebase, etc.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(this.context!!, gso).signOut()
        //change to login screen
        val i = Intent(context, Login::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    private fun loadData() {
        val mRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            .child("Profile")
        mRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                when {
                    snapshot.exists() -> {
                        binding.profileObj = snapshot.getValue(Profile::class.java)
                        val profile: Profile = snapshot.getValue(Profile::class.java)!!
                        url = profile.url
                        when {
                            url.contentEquals("empty") -> {
                                view!!.findViewById<ConstraintLayout>(R.id.progress_circle_profile).visibility = View.INVISIBLE
                            }
                            else -> {
                                Picasso.get().load(url).fit().centerCrop().into(binding.imageViewT, object: Callback{
                                    override fun onSuccess() {
                                        view!!.findViewById<ConstraintLayout>(R.id.progress_circle_profile).visibility = View.INVISIBLE
                                    }

                                    override fun onError(e: Exception?) {
                                    }
                                })
                            }
                        }
                    }
                }
            }
        })
    }
}


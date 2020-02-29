package pablo.myexample.booksmawt.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TwitterAuthProvider
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_base.*
import pablo.myexample.booksmawt.Communicator
import pablo.myexample.booksmawt.Login
import pablo.myexample.booksmawt.Profile

import pablo.myexample.booksmawt.R
import pablo.myexample.booksmawt.databinding.ProfileFragmentBinding
import kotlin.math.sign

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding
    private lateinit var profile: Profile
    private lateinit var model: Communicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(activity!!).get(Communicator::class.java)
        profile = Profile("Paul Lopez", "LA", "null")
        binding.apply {
            profileObj = profile
            editProfileButton.setOnClickListener {
                toEditProfile()
            }
            signOutButton.setOnClickListener {
                Logout()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    private fun toEditProfile() {
        model.passProfileObj(profile)
        activity!!.bottom_nav_view.visibility = View.INVISIBLE
        view!!.findNavController().navigate(R.id.action_navigation_profile_to_editProfile)
    }

    private fun Logout() {
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
}

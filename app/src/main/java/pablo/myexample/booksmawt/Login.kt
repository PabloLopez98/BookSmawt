package pablo.myexample.booksmawt


import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseError
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.*
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.concurrent.schedule

class Login : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var layout: ConstraintLayout
    //Twitter Sign In
    private lateinit var provider: OAuthProvider
    //Google Sign In
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_LOGIN: Int = 9001

    private fun snackBar(str: String) {
        Snackbar.make(
            login_layout,
            str,
            Snackbar.LENGTH_LONG
        ).show()
    }

    //Twitter sign in button's onClick
    fun twitterLogin(view: View) {
        mAuth.startActivityForSignInWithProvider(this, provider)
            .addOnSuccessListener { authResult ->
                layout.visibility = View.VISIBLE
                val name: String = authResult.user?.displayName ?: "No Name"
                val uid = mAuth.currentUser?.uid.toString()
                val dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(snapshotError: DatabaseError) {
                        layout.visibility = View.INVISIBLE
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        when {
                            snapshot.exists() -> {
                                layout.visibility = View.INVISIBLE
                                Login()
                            }
                            else -> {
                                val profile = Profile(name, "Los Angeles, CA", "empty")
                                dbRef.child("Profile").setValue(profile)
                                snackBar("Logging In...")
                                Timer().schedule(2000) {
                                    layout.visibility = View.INVISIBLE
                                    Login()
                                }
                            }
                        }
                    }
                })
            }.addOnFailureListener { exception ->
                layout.visibility = View.INVISIBLE
                snackBar(exception.localizedMessage.toString())
            }
    }

    //Google sign in button's onClick
    fun googleLogin(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_LOGIN)
    }

    //after googleLogin
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LOGIN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                snackBar("Google Sign In failed, please try again")
            }
        }
    }

    //after onActivityResult
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    layout.visibility = View.VISIBLE
                    //progress circle here
                    val name = acct.displayName ?: "No Name"
                    val uid = mAuth.currentUser?.uid.toString()
                    val dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            when {
                                snapshot.exists() -> {
                                    layout.visibility = View.INVISIBLE
                                    Login()
                                }
                                else -> {
                                    val profile = Profile(name, "Los Angeles, CA", "empty")
                                    dbRef.child("Profile").setValue(profile)
                                    Timer().schedule(2000) {
                                        layout.visibility = View.INVISIBLE
                                        Login()
                                    }
                                }
                            }
                        }
                    })
                } else {
                    snackBar("Sign In failed, please try again")
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initialize()
        checkOrRequestPermission()
    }

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1

    private fun checkOrRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // do related task you need to do.
                } else {
                    closeAppDialog()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun toTermsOfUse(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/booksmawttoc/home")))
    }

    fun toPrivacyPolicy(view: View) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/privacy-policy/847dc8d8e6ff68575fd4e059ca96a5da")))
    }

    private fun closeAppDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("BookSmawt cannot function properly without reading external storage.")
            .setPositiveButton("Exit App", { dialog, id -> finish() })
            .setOnDismissListener {
                finish()
            }
        val alert = dialogBuilder.create()
        alert.setTitle("Permission Denied")
        alert.show()
    }

    private fun initialize() {
        layout = findViewById(R.id.progress_circle)
        emailET = findViewById(R.id.login_email_input)
        passwordET = findViewById(R.id.login_password_input)
        mAuth = FirebaseAuth.getInstance()
        //Twitter Sign In
        provider = OAuthProvider.newBuilder("twitter.com", mAuth).build()
        //Google Sign In
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    //Login Button's onClick
    fun checkEmptyFields(view: View) {
        when {
            TextUtils.isEmpty(emailET.text.toString()) -> snackBar("Email is empty")
            TextUtils.isEmpty(passwordET.text.toString()) -> snackBar("Password is empty")
            else -> {
                signInProcess()
            }
        }
    }

    //after checkEmptyFields
    private fun signInProcess() {
        layout.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        layout.visibility = View.INVISIBLE
                        Login()
                    }
                    else -> {
                        layout.visibility = View.INVISIBLE
                        snackBar(task.exception.toString())
                    }
                }
            }
    }

    //after signInProcess
    private fun Login() {
        startService(Intent(this, Service::class.java))
        //transition
        val i = Intent(this, Base::class.java)
        startActivity(i)
        finish()
    }

    fun toRegister(view: View) {
        val i = Intent(this, CreateAccount::class.java)
        startActivity(i)
    }
}

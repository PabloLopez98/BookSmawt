package pablo.myexample.booksmawt


import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import kotlin.concurrent.schedule

class Login : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleButton: Button
    private lateinit var twitterButton: Button
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_LOGIN: Int = 9001

    fun googleLogin(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_LOGIN)
    }

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

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val name = acct.displayName ?: "No Name"
                    val uid = mAuth.currentUser?.uid.toString()
                    val dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid)
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(snapshotError: DatabaseError) {
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            when {
                                snapshot.exists() -> {
                                    Login()
                                }
                                else -> {
                                    val profile = Profile(name, "Los Angeles, CA", "empty")
                                    dbRef.child("Profile").setValue(profile)
                                    snackBar("Logging In...")
                                    Timer().schedule(2000) {
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

    fun twitterLogin(view: View) {

    }

    private fun snackBar(str: String) {
        Snackbar.make(
            login_layout,
            str,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun initialize() {
        emailET = findViewById(R.id.login_email_input)
        passwordET = findViewById(R.id.login_password_input)
        mAuth = FirebaseAuth.getInstance()
        googleButton = findViewById(R.id.google_login_button)
        twitterButton = findViewById(R.id.twitter_login_button)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initialize()
    }

    fun checkEmptyFields(view: View) {
        when {
            TextUtils.isEmpty(emailET.text.toString()) -> snackBar("Email is empty")
            TextUtils.isEmpty(passwordET.text.toString()) -> snackBar("Password is empty")
            else -> {
                signInProcess()
            }
        }
    }

    private fun signInProcess() {
        mAuth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> Login()
                    else -> {
                        snackBar(task.exception.toString())
                    }
                }
            }
    }

    fun toRegister(view: View) {
        val i = Intent(this, CreateAccount::class.java)
        startActivity(i)
    }

    private fun Login() {
        val i = Intent(this, Base::class.java)
        startActivity(i)
        finish()
    }
}

package pablo.myexample.booksmawt

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.*
import kotlin.concurrent.schedule

class CreateAccount : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var confirmPasswordET: EditText
    private lateinit var mAuth: FirebaseAuth

    fun toTermsOfUse(view: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/booksmawttoc/home")
            )
        )
    }

    fun toPrivacyPolicy(view: View) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.termsfeed.com/privacy-policy/847dc8d8e6ff68575fd4e059ca96a5da")
            )
        )
    }

    private fun snackBar(str: String) {
        Snackbar.make(
            create_account_layout,
            str,
            Snackbar.LENGTH_LONG
        ).show()
    }

    fun backToLogin(view: View) {
        val i = Intent(this, Login::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        window.decorView.apply {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
            setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    Handler().postDelayed(
                        {
                            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE
                        }, 5000
                    )
                }
            }
        }
        initialize()
    }

    private fun initialize() {
        usernameET = findViewById(R.id.username_input)
        emailET = findViewById(R.id.email_input)
        passwordET = findViewById(R.id.password_input_one)
        confirmPasswordET = findViewById(R.id.password_input_two)
        mAuth = FirebaseAuth.getInstance()
    }

    fun checkEmptyFields(view: View) {
        when {
            TextUtils.isEmpty(usernameET.text.toString()) -> snackBar("Username is empty")
            TextUtils.isEmpty(emailET.text.toString()) -> snackBar("Email is empty")
            TextUtils.isEmpty(passwordET.text.toString()) -> snackBar("Password is empty")
            TextUtils.isEmpty(confirmPasswordET.text.toString()) -> snackBar("Confirm Password is empty")
            else -> {
                checkPasswordLength(view)
            }
        }
    }

    private fun checkPasswordLength(view: View) {
        when {
            passwordET.length() < 6 -> {
                snackBar("Password is less than 6 characters")
            }
            else -> {
                checkPasswordConfirmation(view)
            }
        }
    }

    private fun checkPasswordConfirmation(view: View) {
        when {
            passwordET.text.toString().contentEquals(confirmPasswordET.text.toString()) -> {
                createAccount(view)
            }
            else -> {
                snackBar("Password not confirmed correctly")
            }
        }
    }

    private fun createAccount(view: View) {
        findViewById<ConstraintLayout>(R.id.progress_circle_ca).visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> updateDatabase(view)
                    else -> {
                        findViewById<ConstraintLayout>(R.id.progress_circle_ca).visibility =
                            View.INVISIBLE
                        snackBar(task.exception.toString())
                    }
                }
            }
    }

    private fun updateDatabase(view: View) {
        val profile = Profile(usernameET.text.toString(), "Los Angeles, CA, USA", "empty")
        val id: String = mAuth.currentUser?.uid.toString()
        val mRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(id).child("Profile")
        mRef.setValue(profile)
        Timer().schedule(3000) {
            backToLogin(view)
        }
    }
}

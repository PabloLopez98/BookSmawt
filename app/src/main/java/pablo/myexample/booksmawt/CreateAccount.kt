package pablo.myexample.booksmawt

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.InputDevice
import android.view.View
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccount : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var confirmPasswordET: EditText
    private lateinit var mAuth: FirebaseAuth

    fun backToLogin(view: View) {
        val i = Intent(this, Login::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
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
            TextUtils.isEmpty(usernameET.text.toString()) -> Snackbar.make(
                create_account_layout,
                "Username is empty",
                Snackbar.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(emailET.text.toString()) -> Snackbar.make(
                create_account_layout,
                "Email is empty",
                Snackbar.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(passwordET.text.toString()) -> Snackbar.make(
                create_account_layout,
                "Password is empty",
                Snackbar.LENGTH_LONG
            ).show()
            TextUtils.isEmpty(confirmPasswordET.text.toString()) -> Snackbar.make(
                create_account_layout,
                "Confirm Password is empty",
                Snackbar.LENGTH_LONG
            ).show()
            else -> {
                checkPasswordLength(view)
            }
        }
    }

    fun checkPasswordLength(view: View) {
        when {
            passwordET.length() < 6 -> {
                Snackbar.make(
                    create_account_layout,
                    "Password is less than 6 characters",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            else -> {
                checkPasswordConfirmation(view)
            }
        }
    }

    fun checkPasswordConfirmation(view: View) {
        when {
            passwordET.text.toString().contentEquals(confirmPasswordET.text.toString()) -> {
                createAccount(view)
            }
            else -> {
                Snackbar.make(
                    create_account_layout,
                    "Password not confirmed correctly",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    fun createAccount(view: View) {
        mAuth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> true//update profile in database
                    // val userId = mAuth.currentUser!!.uid
                    //update user profile information
                    //val currentUserDb = mDatabaseReference!!.child(userId)
                    //currentUserDb.child("firstName").setValue(firstName)
                    //currentUserDb.child("lastName").setValue(lastName)
                    else -> {
                        // If sign in fails, display a message to the user.
                    }
                }
            }
    }
}

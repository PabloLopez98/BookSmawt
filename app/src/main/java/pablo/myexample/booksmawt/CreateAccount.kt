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

    private fun snackBar(str: String){
        Snackbar.make(
            create_account_layout,
            str,
            Snackbar.LENGTH_LONG).show()
    }

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
        mAuth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> updateDatabase(view)
                    else -> {
                         snackBar(task.exception.toString())
                    }
                }
            }
    }

    private fun updateDatabase(view: View){
        val profile = Profile(usernameET.text.toString(), "Los Angeles, CA", "empty")
        val id: String = mAuth.currentUser?.uid.toString()
        val mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Profile")
        mRef.setValue(profile)
        snackBar("Account Created! Redirecting to Login Screen.")
        Timer().schedule(3000) {
            backToLogin(view)
        }
    }
}

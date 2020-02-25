package pablo.myexample.booksmawt


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_account.*

class Login : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleButton: Button
    private lateinit var twitterButton: Button

    fun googleLogin(view: View){

    }

    fun twitterLogin(view: View){

    }

    private fun snackBar(str: String) {
        Snackbar.make(
            create_account_layout,
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

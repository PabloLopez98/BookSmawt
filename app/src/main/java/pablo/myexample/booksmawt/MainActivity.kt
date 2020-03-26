package pablo.myexample.booksmawt

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        check()
    }

    private fun check() {
        when {
            FirebaseAuth.getInstance().currentUser != null -> toBase()
            else -> {
                Timer().schedule(2500) {
                    toLogin()
                }
            }
        }
    }

    private fun toBase() {
        val i = Intent(this, Base::class.java)
        startActivity(i)
        finish()
    }

    private fun toLogin() {
        val i = Intent(this, Login::class.java)
        startActivity(i)
        finish()
    }
}

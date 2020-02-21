package pablo.myexample.booksmawt


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
   /*     window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT*/
    }

    fun toRegister(view: View) {
        val i = Intent(this, CreateAccount::class.java)
        startActivity(i)
    }

    fun Login(view: View) {
        val i = Intent(this, Base::class.java)
        startActivity(i)
    }
}

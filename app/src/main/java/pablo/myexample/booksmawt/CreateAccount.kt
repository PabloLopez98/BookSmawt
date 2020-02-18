package pablo.myexample.booksmawt

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CreateAccount : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        //setSupportActionBar(findViewById(R.id.create_account_toolbar))
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun backToLogin(view: View) {
        val i = Intent(this, Login::class.java)
        startActivity(i)
    }
}

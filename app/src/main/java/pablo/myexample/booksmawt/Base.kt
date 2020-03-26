package pablo.myexample.booksmawt

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class Base : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
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
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        bottomNavView?.setupWithNavController(navController)
    }

    override fun onStop() {
        super.onStop()
        Log.i("ONSTOP", "STARTSERVICE")
        startService(Intent(this, Service::class.java))
    }

    override fun onResume() {
        super.onResume()
        Log.i("ONRESUME", "STOPSERVICE")
        stopService(Intent(this, Service::class.java))
    }

    /*
    login method in login activity has service code,
    as well as the profile logout method in profile fragment
    4 parts total: login, logout, onstop, onresume
     */

}

package com.example.oluwafemioyebayoinfo6214project2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap

class MainActivity : AppCompatActivity() {

    val googleMapsFragment =   GoogleMapsFragment()
    val googlePlacesFragment = GooglePlacesFragment()
    val emailFragment = EmailFragment()
    val aboutFragment = AboutFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, googleMapsFragment);
            commit();
        }

        val menuBtn = findViewById<ImageButton>(R.id.menuButton)
        val menuOptions = PopupMenu(this, menuBtn)

        menuOptions.menuInflater.inflate(R.menu.menu_options, menuOptions.menu)
        menuOptions.setOnMenuItemClickListener { meniItems ->
            when(meniItems.itemId)
            {
                R.id.google_map_option -> changeFragment(googleMapsFragment)
                R.id.google_places_option -> changeFragment(googlePlacesFragment)
                R.id.email_option -> changeFragment(emailFragment)
                R.id.about_option -> changeFragment(aboutFragment)

            }
            true
        }

        menuBtn.setOnClickListener {
            menuOptions.show()
        }

    }

    private fun changeFragment(frgamentName: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, frgamentName);
            commit();
        }
    }
}
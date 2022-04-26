package com.bibliotecadebolso.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityHomeBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity
import com.bibliotecadebolso.app.util.Constants

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavView = binding.navView
        drawerLayout = binding.container

        setupBottomBarNavigation()
        setupNavigationDrawer()



    }

    private fun setupBottomBarNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
    }

    private fun setupNavigationDrawer() {
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.drawer_label_open,
            R.string.drawer_label_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationOptions()

    }

    private fun setupNavigationOptions() {
        binding.navHomeView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mi_logout -> {
                    removeTokensFromSharedPreferences()
                    returnToLoginAccessActivity()
                }
                else -> {
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.topbar_main_menu, menu)
//
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun removeTokensFromSharedPreferences() {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)

        with(prefs.edit()) {
            remove(Constants.Prefs.Tokens.ACCESS_TOKEN)
            remove(Constants.Prefs.Tokens.REFRESH_TOKEN)
            apply()
        }
    }

    private fun returnToLoginAccessActivity() {
        val homeIntent = Intent(this, AppAccessActivity::class.java)

        startActivity(homeIntent)
        finish()
    }
}
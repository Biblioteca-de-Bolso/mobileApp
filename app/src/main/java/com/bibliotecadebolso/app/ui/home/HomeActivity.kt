package com.bibliotecadebolso.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityHomeBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity
import com.bibliotecadebolso.app.ui.home.nav.DeleteAccountActivity
import com.bibliotecadebolso.app.ui.info.AboutActivity
import com.bibliotecadebolso.app.util.Constants
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var toolBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavView = binding.navView
        drawerLayout = binding.container
        setupTopBarNavigation()
        setupBottomBarNavigation()

        setupTopBarNavigationDrawer()
    }

    private fun setupTopBarNavigation() {
        toolBar = binding.topBarApp
        setSupportActionBar(toolBar)


        /*
            BORDER TOOLBAR SETUṔ
            In order to remove the margin between screen and toolbar, you must
            remove android:layout_margin from AppBarLayout in Activity
         */
        val radius: Float = resources.getDimension(R.dimen.default_toolbar_corner_radius)
        val materialShapeDrawable: MaterialShapeDrawable =
            toolBar.background as MaterialShapeDrawable

        materialShapeDrawable.shapeAppearanceModel = materialShapeDrawable.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        binding.topBarLayout.outlineProvider = null
    }


    private fun setupBottomBarNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.navigation_home,
                R.id.navigation_annotation,
                R.id.navigation_borrow,
                R.id.navigation_profile,
                )
        ).setOpenableLayout(drawerLayout)
            .build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
    }

    private fun setupTopBarNavigationDrawer() {
        setupNavigationOptions()

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.drawer_label_open,
            R.string.drawer_label_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        actionBarDrawerToggle.apply {
            isDrawerIndicatorEnabled = true
            toolbarNavigationClickListener =
                View.OnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        }
        actionBarDrawerToggle.syncState()


    }

    private fun setupNavigationOptions() {
        binding.navHomeView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mi_logout -> {
                    removeTokensFromSharedPreferences()
                    returnToLoginAccessActivity()
                }
                R.id.mi_disable_account -> {
                    val intent = Intent(this, DeleteAccountActivity::class.java)
                    startActivity(intent)
                }
                R.id.mi_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
                else -> {

                }
            }
            true
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
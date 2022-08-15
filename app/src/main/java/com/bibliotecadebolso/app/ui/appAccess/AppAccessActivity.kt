package com.bibliotecadebolso.app.ui.appAccess

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityLoginBinding
import com.bibliotecadebolso.app.util.ContextUtils

class AppAccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var appAccessViewModel: AppAccessViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextUtils.setActionBarColor(supportActionBar, this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        appAccessViewModel = ViewModelProvider(this)[AppAccessViewModel::class.java]



        val navHostFragment = supportFragmentManager.findFragmentById(R.id.access_transaction_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setTheme(R.style.Theme_MyApplication)
        setupActionBarWithNavController(navController)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() ||  super.onSupportNavigateUp()
    }
}
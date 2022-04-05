package com.bibliotecadebolso.app.ui.appAccess

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityLoginBinding

class AppAccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var appAccessViewModel: AppAccessViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        appAccessViewModel = ViewModelProvider(this)[AppAccessViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.access_transaction_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() ||  super.onSupportNavigateUp()
    }
}
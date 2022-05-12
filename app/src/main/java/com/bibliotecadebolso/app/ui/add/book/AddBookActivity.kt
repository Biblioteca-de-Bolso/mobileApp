package com.bibliotecadebolso.app.ui.add.book

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityAddBookBinding
import com.google.android.material.snackbar.Snackbar

class AddBookActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var viewModel: AddBookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AddBookViewModel::class.java]
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_add_book)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_add_book)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
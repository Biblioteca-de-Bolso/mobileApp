package com.bibliotecadebolso.app.ui.appAccess

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadebolso.app.databinding.ActivityLoginBinding

class AppAccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var appAccessViewModel: AppAccessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        appAccessViewModel = ViewModelProvider(this)[AppAccessViewModel::class.java]
        setContentView(binding.root)
    }
}
package com.bibliotecadebolso.app.ui.book.linearList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityBookLinearListBinding

class BookLinearListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookLinearListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookLinearListBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}
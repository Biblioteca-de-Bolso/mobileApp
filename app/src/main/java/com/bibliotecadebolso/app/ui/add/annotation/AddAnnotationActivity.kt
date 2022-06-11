package com.bibliotecadebolso.app.ui.add.annotation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding

class AddAnnotationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnotationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddAnnotationContentFragment.newInstance())
                .commitNow()
        }
    }
}
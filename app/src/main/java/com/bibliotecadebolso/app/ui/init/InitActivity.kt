package com.bibliotecadebolso.app.ui.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity

class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)


        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AppAccessActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000L)
    }
}
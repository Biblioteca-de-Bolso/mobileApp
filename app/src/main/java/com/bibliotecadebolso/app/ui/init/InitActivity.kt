package com.bibliotecadebolso.app.ui.init

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity
import com.bibliotecadebolso.app.ui.home.HomeActivity
import com.bibliotecadebolso.app.util.Constants

class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)


        supportActionBar?.hide()

        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)

        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")
        val refreshToken = prefs.getString(Constants.Prefs.Tokens.REFRESH_TOKEN, "")

        //TODO check if accessToken is valid
            //TODO else: check if refreshToken is valid. If is valid, refresh the accessToken
        //TODO if any token return invalid, then return to login Activity
        val intent: Intent = if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) {
            Intent(this, AppAccessActivity::class.java)
        } else {
            Intent(this, HomeActivity::class.java)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 3000L)
    }
}
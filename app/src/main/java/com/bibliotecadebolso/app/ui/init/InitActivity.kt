package com.bibliotecadebolso.app.ui.init

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.jwt.JWT
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityInitBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity
import com.bibliotecadebolso.app.ui.home.HomeActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result


class InitActivity : AppCompatActivity() {

    private lateinit var viewModel: InitViewModel
    private lateinit var binding: ActivityInitBinding
    private var isWaitingRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[InitViewModel::class.java]

        supportActionBar?.hide()
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!
        val refreshToken = prefs.getString(Constants.Prefs.Tokens.REFRESH_TOKEN, "")!!

        getNewAccessTokenIfExpiredOrRedirectToHome(accessToken, refreshToken)
        authTokensListener(prefs)
    }

    private fun getNewAccessTokenIfExpiredOrRedirectToHome(
        accessToken: String,
        refreshToken: String
    ) {
        val accessDecoded = JWT(accessToken)

        if (accessDecoded.isExpired(0) && refreshToken.isNotEmpty()) {
            viewModel.getNewAcessToken(refreshToken)
            isWaitingRequest = true
            binding.pgLoading.visibility = View.VISIBLE
        } else {
            isWaitingRequest = false
            binding.pgLoading.visibility = View.INVISIBLE
            redirectToHomeActivityIfHasNoPendingRequest(accessToken, refreshToken)
        }
    }

    private fun authTokensListener(prefs: SharedPreferences) {
        viewModel.liveDataAuthTokens.observe(this) {
            var newAccessToken = ""
            var newRefreshToken = ""

            if (it is Result.Success) {
                newAccessToken = it.response!!.accessToken
                newRefreshToken = it.response.refreshToken
            }

            val accessTokenLabel = Constants.Prefs.Tokens.ACCESS_TOKEN
            val refreshTokenLabel = Constants.Prefs.Tokens.ACCESS_TOKEN
            with(prefs.edit()) {
                putString(accessTokenLabel, newAccessToken)
                putString(refreshTokenLabel, newRefreshToken)
                apply()
            }
            isWaitingRequest = false;
            binding.pgLoading.visibility = View.INVISIBLE
            redirectToHomeActivityIfHasNoPendingRequest(
                prefs.getString(accessTokenLabel, "")!!,
                prefs.getString(refreshTokenLabel, "")!!
            )
        }
    }

    private fun redirectToHomeActivityIfHasNoPendingRequest(
        accessToken: String,
        refreshToken: String
    ) {
        if (!isWaitingRequest) {
            val intent: Intent = if (accessToken.isEmpty() || refreshToken.isEmpty()) {
                Toast.makeText(this, getString(R.string.alert_token_expired), Toast.LENGTH_LONG)
                    .show()
                Intent(this, AppAccessActivity::class.java)
            } else {
                Intent(this, HomeActivity::class.java)
            }

            val options =
                ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
            startActivity(intent, options.toBundle())
            finish()
        }
    }
}
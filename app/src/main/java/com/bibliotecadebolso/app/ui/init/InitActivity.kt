package com.bibliotecadebolso.app.ui.init

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityInitBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity
import com.bibliotecadebolso.app.ui.home.HomeActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils


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
        val authTokens = SharedPreferencesUtils.getAuthTokens(prefs)

        getNewAccessTokenIfExpiredOrRedirectToHome(authTokens.accessToken, authTokens.refreshToken)
        listenerRedirectWhenRefreshTokenRequestEnds(prefs)
    }

    private fun getNewAccessTokenIfExpiredOrRedirectToHome(
        accessToken: String,
        refreshToken: String
    ) {
        val accessDecoded: JWT
        if (accessToken.isEmpty()) {
            redirectToLoginOrHomeActivity("", true)
            return
        }
        try {
            accessDecoded = JWT(accessToken)
        } catch (e: DecodeException) {
            redirectToHomeActivityIfHasNoPendingRequest(getString(R.string.error_invalid_token), true)
            return
        }

        if (refreshToken.isNotEmpty()) {
            viewModel.getNewAccessToken(accessToken, refreshToken)
            setIsWaitingRequest(true)
        } else {
            setIsWaitingRequest(false)
            redirectToHomeActivityIfHasNoPendingRequest("", false)
        }
    }

    private fun setIsWaitingRequest(isWaitingRequest: Boolean) {
        this.isWaitingRequest = isWaitingRequest
        binding.pgLoading.visibility = if (isWaitingRequest) View.VISIBLE else View.INVISIBLE
    }

    private fun listenerRedirectWhenRefreshTokenRequestEnds(prefs: SharedPreferences) {
        viewModel.liveDataAuthTokens.observe(this) {
            var newAccessToken = ""
            var newRefreshToken = ""
            var errorMessage = ""
            var hasError = false

            if (it is Result.Success) {
                newAccessToken = it.response!!.accessToken
                newRefreshToken = it.response.refreshToken
            } else if (it is Result.Error) {
                hasError = true
                when (it.errorBody.code) {
                    "noInternetConnection" -> errorMessage =
                        getString(R.string.label_no_internet_connection)
                    else -> errorMessage = it.errorBody.message
                }
            }

            if ((it is Result.Success) ||
                (it is Result.Error && isNoInternetError(it))
            )
                SharedPreferencesUtils.putAuthTokens(prefs, newAccessToken, newRefreshToken)

            setIsWaitingRequest(false)
            redirectToHomeActivityIfHasNoPendingRequest(errorMessage, hasError)
        }
    }

    private fun isNoInternetError(result: Result.Error) = result.errorBody.code != "noInternetConnection"

    private fun redirectToHomeActivityIfHasNoPendingRequest(errorMessage: String, hasError: Boolean) {
        if (isWaitingRequest) return
        redirectToLoginOrHomeActivity(errorMessage, hasError)
    }

    private fun redirectToLoginOrHomeActivity(errorMessage: String, hasError: Boolean) {
        val intent: Intent =
            if (hasError) {
                if (errorMessage.isNotEmpty()) showLongToast(errorMessage)
                Intent(this, AppAccessActivity::class.java)
            } else {
                Intent(this, HomeActivity::class.java)
            }

        val activityTransition = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, activityTransition.toBundle())
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 500)
    }

    private fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
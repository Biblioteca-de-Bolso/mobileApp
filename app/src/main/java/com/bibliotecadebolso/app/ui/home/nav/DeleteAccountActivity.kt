package com.bibliotecadebolso.app.ui.home.nav

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.jwt.JWT
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityDeleteAccoutBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteAccoutBinding
    private lateinit var viewModel: DeleteAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeleteAccoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DeleteAccountViewModel::class.java]


        setContentView(binding.root)

        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)

        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!
        val refreshToken = prefs.getString(Constants.Prefs.Tokens.REFRESH_TOKEN, "")!!

        if (accessToken.isEmpty()) finish()

        val jwt = JWT(accessToken)
        val id = jwt.getClaim("id").asInt() ?: -1
        val email = jwt.getClaim("email").asString() ?: ""

        binding.btnRegister.setOnClickListener {
            val password = binding.etPassword.editText?.text.toString()
            binding.progressSending.visibility = View.VISIBLE
            viewModel.deleteAccount(accessToken, id, email, password)
        }

        viewModel.deleteAccountResult.observe(this) {
            binding.progressSending.visibility = View.GONE
            if (it is Result.Success) {
                showLongToast(getString(R.string.label_account_deleted))

                with (prefs.edit()) {
                    putString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")
                    putString(Constants.Prefs.Tokens.REFRESH_TOKEN, "")
                    apply()
                }

                val intent = Intent(this, AppAccessActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)


                ActivityCompat.finishAffinity(this)
            } else if (it is Result.Error) {
                showLongToast(it.errorBody.message)
            }
        }
    }


    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    private fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}
package com.bibliotecadebolso.app.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityChangePasswordBinding
import com.bibliotecadebolso.app.ui.user.requestChangePassword.RequestChangePasswordViewModel
import com.bibliotecadebolso.app.util.Result

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: RequestChangePasswordViewModel
    private var queryEmail = ""
    private var queryRecoverCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RequestChangePasswordViewModel::class.java]

        val tilEmail = binding.tilEmail
        queryEmail = intent.data?.getQueryParameter("email") ?: ""
        val textEmail = queryEmail.ifEmpty { "No email" }
        queryRecoverCode = intent.data?.getQueryParameter("recoverCode") ?: ""
        tilEmail.editText?.setText(textEmail)

        binding.btnChangePassword.setOnClickListener {
            binding.progressSending.visibility = View.VISIBLE
            viewModel.changePassword(queryEmail, queryRecoverCode, binding.tilPassword.editText!!.text.toString())
        }

        viewModel.changePasswordLiveData.observe(this) {
            binding.progressSending.visibility = View.GONE
            when (it) {
                is Result.Success -> {
                    Toast.makeText(this, getString(R.string.label_password_changed), Toast.LENGTH_LONG).show()
                    finish()
                }
                is Result.Error -> Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
            }
        }

        setContentView(binding.root)
    }
}
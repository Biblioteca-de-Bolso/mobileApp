package com.bibliotecadebolso.app.ui.user.requestChangePassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityRequestChangePasswordBinding
import com.bibliotecadebolso.app.ui.user.requestChangePassword.form.RequestChangePasswordForm
import com.bibliotecadebolso.app.util.Result

class RequestChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestChangePasswordBinding
    private lateinit var viewModel: RequestChangePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRequestChangePasswordBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RequestChangePasswordViewModel::class.java]

        binding.tilEmail.editText!!.addTextChangedListener { text ->
            binding.tilEmail.error =
                if (text.toString().isEmpty()) getString(R.string.label_empty)
                else ""
        }

        binding.btnSendRequest.setOnClickListener {
            val email = binding.tilEmail.editText!!.text.toString()
            if (email.isEmpty())
                Toast.makeText(
                    this,
                    getString(R.string.label_empty_email),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                binding.progressSending.visibility = View.VISIBLE
                viewModel.requestChangePassword(RequestChangePasswordForm( email))

            }

        }

        viewModel.requestChangePasswordLiveData.observe(this) {
            binding.progressSending.visibility = View.GONE
            when (it) {
                is Result.Success -> {
                    Toast.makeText(this, getString(R.string.label_check_email_to_change_password), Toast.LENGTH_LONG).show()
                    finish()
                }
                is Result.Error -> Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
            }

        }

        setContentView(binding.root)
    }
}
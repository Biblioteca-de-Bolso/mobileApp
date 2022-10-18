package com.bibliotecadebolso.app.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils
import com.bibliotecadebolso.app.data.validator.validations.EmailValidation
import com.bibliotecadebolso.app.data.validator.validations.PasswordEqualsToConfirmPasswordValidator
import com.bibliotecadebolso.app.data.validator.validations.PasswordValidator
import com.bibliotecadebolso.app.databinding.ActivityChangePasswordBinding
import com.bibliotecadebolso.app.ui.user.requestChangePassword.RequestChangePasswordViewModel
import com.bibliotecadebolso.app.util.Result
import com.google.android.material.textfield.TextInputLayout

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
            val tilPassword = binding.tilPassword
            val tilConfirmPassword = binding.tilConfirmPassword
            val passwordString = tilPassword.editText!!.text.toString()
            val confirmPasswordString = tilConfirmPassword.editText!!.text.toString()

            val validators: Map<TextInputLayout, IValidator> = mapOf(
                tilEmail to EmailValidation(tilEmail.editText!!.text.toString()),
                tilPassword to PasswordValidator(passwordString),
                tilConfirmPassword to PasswordEqualsToConfirmPasswordValidator(
                    passwordString,
                    confirmPasswordString
                ),
            )

            val hasErrors =
                ValidationResultUtils.showErrorOnTextInputLayoutAndReturnIfHasError(this, validators)

            Log.e("validation", hasErrors.toString())

            if (!hasErrors) {
                binding.progressSending.visibility = View.VISIBLE
                viewModel.changePassword(
                    queryEmail,
                    queryRecoverCode,
                    binding.tilPassword.editText!!.text.toString()
                )
            }
        }

        viewModel.changePasswordLiveData.observe(this) {
            binding.progressSending.visibility = View.GONE
            when (it) {
                is Result.Success -> {
                    Toast.makeText(
                        this,
                        getString(R.string.label_password_changed),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                is Result.Error -> Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        setContentView(binding.root)
    }
}
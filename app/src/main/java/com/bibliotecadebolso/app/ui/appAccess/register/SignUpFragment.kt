package com.bibliotecadebolso.app.ui.appAccess.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.validations.EmailValidation
import com.bibliotecadebolso.app.data.validator.validations.PasswordValidator
import com.bibliotecadebolso.app.data.validator.validations.UsernameValidator
import com.bibliotecadebolso.app.databinding.FragmentSignUpBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessViewModel
import com.bibliotecadebolso.app.util.Result
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private val appAccessViewModel: AppAccessViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        setRegisterResponseObserver()
        setOnClickRegisterListener()


        return binding.root
    }

    private fun setOnClickRegisterListener() {
        binding.btnRegister.setOnClickListener {

            binding.apply {
                etUsername.error = ""
                etEmail.error = ""
                etPassword.error = ""
                etConfirmPassword.error = ""
            }

            val usernameInput = binding.etUsername.editText?.text.toString()
            val emailInput = binding.etEmail.editText?.text.toString()
            val passwordInput = binding.etPassword.editText?.text.toString()
            val confirmPasswordInput = binding.etConfirmPassword.editText?.text.toString()
            var hasError = false

            val validators: Map<TextInputLayout,IValidator> = mapOf(
                binding.etUsername to UsernameValidator(usernameInput),
                binding.etEmail to EmailValidation(emailInput),
                binding.etPassword to PasswordValidator(passwordInput),
                binding.etConfirmPassword to PasswordValidator(confirmPasswordInput)
            )

            if (passwordInput != confirmPasswordInput) {
                binding.etPassword.error = getString(R.string.error_password_and_confirm_password_not_equals)
                binding.etConfirmPassword.error = getString(R.string.error_password_and_confirm_password_not_equals)
                hasError = true
            }

            val inputHasError = showErrorAndReturnIfHasError(validators)
            hasError = (hasError || inputHasError)

            if (!hasError) {
                binding.pgLoading.visibility = View.VISIBLE
                appAccessViewModel.register(usernameInput, emailInput, passwordInput)
            }
        }
    }

    private fun showErrorAndReturnIfHasError(map: Map<TextInputLayout, IValidator>): Boolean {
        var hasError = false
        map.forEach{
            val validationResult = it.value.validate()
            it.key.error = getString(validationResult.message)
            if (!validationResult.isSuccess) hasError = true
        }
        return hasError
    }

    private fun setRegisterResponseObserver() {
        appAccessViewModel.registerResponse.observe(viewLifecycleOwner) {
            binding.pgLoading.visibility = View.GONE
            when (it) {
                is Result.Success<UserObject?> -> {
                    showLongToast(getString(R.string.label_successfully_registered))
                    if (it.response == null) {
                        showLongSnackBar(getString(R.string.label_server_not_return_correctly))
                    }

                }
                is Result.Error -> {
                    showLongSnackBar(it.errorBody.message)
                }
            }
        }
    }

    private fun showLongToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

}
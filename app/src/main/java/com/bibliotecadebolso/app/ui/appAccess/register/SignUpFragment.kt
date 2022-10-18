package com.bibliotecadebolso.app.ui.appAccess.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils
import com.bibliotecadebolso.app.data.validator.validations.EmailValidation
import com.bibliotecadebolso.app.data.validator.validations.PasswordEqualsToConfirmPasswordValidator
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

        setInputListener()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val signUpInputs = appAccessViewModel.singUpInputs

        binding.apply {
            etEmail.editText?.setText(signUpInputs.email)
            etUsername.editText?.setText(signUpInputs.username)
            etPassword.editText?.setText(signUpInputs.password)
            etConfirmPassword.editText?.setText(signUpInputs.confirmPassword)
        }
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

            val validators: Map<TextInputLayout, IValidator> = mapOf(
                binding.etUsername to UsernameValidator(usernameInput),
                binding.etEmail to EmailValidation(emailInput),
                binding.etPassword to PasswordValidator(passwordInput),
                binding.etConfirmPassword to PasswordEqualsToConfirmPasswordValidator(
                    passwordInput,
                    confirmPasswordInput
                )
            )

            val hasError = ValidationResultUtils.showErrorOnTextInputLayoutAndReturnIfHasError(
                requireContext(),
                validators
            )

            if (!hasError) {
                binding.pgLoading.visibility = View.VISIBLE
                appAccessViewModel.register(usernameInput, emailInput, passwordInput)
            }
        }
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

    private fun setInputListener() {
        binding.etEmail.editText?.addTextChangedListener {
            appAccessViewModel.singUpInputs.email = it.toString()
        }
        binding.etPassword.editText?.addTextChangedListener {
            appAccessViewModel.singUpInputs.password = it.toString()
        }
        binding.etUsername.editText?.addTextChangedListener {
            appAccessViewModel.singUpInputs.username = it.toString()
        }
        binding.etConfirmPassword.editText?.addTextChangedListener {
            appAccessViewModel.singUpInputs.confirmPassword = it.toString()
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
package com.bibliotecadebolso.app.ui.appAccess.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.databinding.FragmentSignUpBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessViewModel
import com.bibliotecadebolso.app.util.Result
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

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
            var error = false

            if (passwordInput != confirmPasswordInput) {
                binding.etPassword.error = getString(R.string.invalid_password)
                binding.etConfirmPassword.error = getString(R.string.invalid_password)
                error = true
            }

            if (usernameInput.isEmpty()) {
                binding.etUsername.error = getString(R.string.invalid_username)
                error = true
            }

            if (emailInput.isEmpty()) {
                binding.etEmail.error = getString(R.string.label_empty_email)
                error = true
            }

            if (passwordInput.isEmpty()) {
                binding.etPassword.error = getString(R.string.invalid_password)
                error = true
            }
            if (confirmPasswordInput.isEmpty()) {
                binding.etConfirmPassword.error = getString(R.string.invalid_password)
                error = true
            }

            if (!error) {
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

    private fun showLongToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

}
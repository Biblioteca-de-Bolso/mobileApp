package com.bibliotecadebolso.app.ui.appAccess.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.databinding.FragmentLoginBinding
import com.bibliotecadebolso.app.data.model.SessionManager
import com.bibliotecadebolso.app.ui.appAccess.AppAccessViewModel
import com.bibliotecadebolso.app.ui.home.HomeActivity

class LoginFragment : Fragment() {

    private val appAccessViewModel: AppAccessViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )

        sessionManager = SessionManager(requireContext())
        setupLoginResponseObserver()
        setupOnClickLoginListener()

        return binding.root
    }

    private fun setupLoginResponseObserver() {
        appAccessViewModel.loginResponse.observe(viewLifecycleOwner) {
            binding.pgLoading.visibility = View.GONE
            when (it) {
                is Result.Success<*> -> {
                    showLongToast("You're logged")
                    //TODO need to save tokens on BD
                    sessionManager.saveAuthTokens(it.data as AuthTokens)
                    navigateToHome()
                }
                is Result.Error -> {
                    showLongToast("Incorrect credentials")
                }
            }
        }
    }

    private fun showLongToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun navigateToHome() {
        val intent = Intent(
            requireContext(),
            HomeActivity::class.java
        )

        startActivity(intent)
    }

    private fun setupOnClickLoginListener() {
        val email = binding.etEmail
        val password = binding.etPassword
        val buttonLogin = binding.btnLogin
        val loading = binding.pgLoading
        val buttonRegister = binding.btnRegister

        buttonLogin.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            val isValidParameters = appAccessViewModel.isEmailValid(emailText)
                    && appAccessViewModel.isPasswordValid(passwordText)

            if (!isValidParameters) showLongToast("Your email or password is not a valid parameter")
            else {
                loading.visibility = View.VISIBLE
                appAccessViewModel.login(emailText, passwordText)
            }
        }

        buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
    }


}
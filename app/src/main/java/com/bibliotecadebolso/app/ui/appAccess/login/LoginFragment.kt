package com.bibliotecadebolso.app.ui.appAccess.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.SessionManager
import com.bibliotecadebolso.app.databinding.FragmentLoginBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessViewModel
import com.bibliotecadebolso.app.ui.home.HomeActivity
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private val appAccessViewModel: AppAccessViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())

        setupLoginResponseObserver()

        setupOnClickLoginListener()
        setupOnClickRegisterListener()

        return binding.root
    }

    private fun setupLoginResponseObserver() {
        appAccessViewModel.loginResponse.observe(viewLifecycleOwner) {
            binding.pgLoading.visibility = View.GONE
            closeKeyboard()
            when (it) {
                is Result.Success<*> -> {
                    showLongToast(getString(R.string.label_user_logged))
                    //TODO need to save tokens on BD
                    sessionManager.saveAuthTokens(it.data as AuthTokens)
                    navigateToHome()
                }
                is Result.Error -> {
                    showLongSnackBar(it.errorBody.message)
                }
            }
        }
    }

    private fun setupOnClickLoginListener() {

        val email = binding.etEmail.editText
        val password = binding.etPassword.editText
        val buttonLogin = binding.btnLogin
        val loading = binding.pgLoading

        buttonLogin.setOnClickListener {
            if (!RequestUtils.deviceIsConnected(requireContext())) {
                showLongSnackBar(getString(R.string.label_you_dont_have_connection))
            } else {
                val emailText = email?.text.toString().trim()
                val passwordText = password?.text.toString().trim()

                val isValidParameters = appAccessViewModel.isEmailValid(emailText)
                        && appAccessViewModel.isPasswordValid(passwordText)

                if (!isValidParameters)
                    showLongSnackBar(getString(R.string.label_incorrect_credentials))
                else {
                    loading.visibility = View.VISIBLE
                    appAccessViewModel.login(emailText, passwordText)
                }
            }
        }
    }


    private fun setupOnClickRegisterListener() {
        val buttonRegister = binding.btnRegister

        buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun showLongToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }

    private fun closeKeyboard() {
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow((binding.root as View).windowToken, 0)
    }

}
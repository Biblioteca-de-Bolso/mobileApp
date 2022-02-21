package com.bibliotecadebolso.app.ui.login

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
import com.bibliotecadebolso.app.data.Result
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.databinding.FragmentLoginBinding
import com.bibliotecadebolso.app.model.SessionManager
import com.bibliotecadebolso.app.ui.home.HomeActivity

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        sessionManager = SessionManager(requireContext())

        val email = binding.email
        val password = binding.password
        val buttonLogin = binding.login
        val loading = binding.loading
        val buttonRegister = binding.register


        loginViewModel.loginResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success<*> -> {
                    Toast.makeText(requireContext(), "You are logged", Toast.LENGTH_LONG).show()
                    sessionManager.saveAuthTokens(it.data as AuthTokens)
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Credentials incorrect", Toast.LENGTH_LONG).show()
                }
            }
        }

        buttonLogin.setOnClickListener {
            val isValidParameters = loginViewModel.login(email.text.toString().trim(), password.text.toString().trim())
            if (!isValidParameters) Toast.makeText(requireContext(), "Your email or password is not a valid parameter", Toast.LENGTH_LONG).show()
        }

        buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }


        return binding.root
    }
}
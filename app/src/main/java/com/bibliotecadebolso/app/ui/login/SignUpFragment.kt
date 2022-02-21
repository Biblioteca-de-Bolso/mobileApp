package com.bibliotecadebolso.app.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentSignUpBinding
import com.bibliotecadebolso.app.model.SessionManager

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private val loginViewModel: LoginViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)


        val username = binding.username
        val email = binding.email
        val password = binding.password
        val buttonRegister = binding.register
        val buttonLogin = binding.login

        buttonRegister.setOnClickListener {
            loginViewModel.register(username.text.toString(), email.text.toString(), password.text.toString() )
        }

        buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        return binding.root
    }

}
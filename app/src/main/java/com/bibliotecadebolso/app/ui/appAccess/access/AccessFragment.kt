package com.bibliotecadebolso.app.ui.appAccess.access

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentAccessBinding
import com.bibliotecadebolso.app.ui.user.requestChangePassword.RequestChangePasswordActivity

class AccessFragment : Fragment() {

    private lateinit var binding: FragmentAccessBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccessBinding.inflate(layoutInflater, container, false)

        setupOnClickLoginListener()
        setupOnClickRegisterListener()

        setupOnClickForgotPasswordListener()

        return binding.root
    }

    private fun setupOnClickLoginListener() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_accessActivity_to_loginFragment)
        }
    }

    private fun setupOnClickRegisterListener() {
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_accessActivity_to_signUpFragment)
        }
    }

    private fun setupOnClickForgotPasswordListener() {
        binding.tvForgotPassword.setOnClickListener{
            val intent = Intent(requireContext(), RequestChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
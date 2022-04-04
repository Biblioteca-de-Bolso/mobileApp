package com.bibliotecadebolso.app.ui.appAccess.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentSignUpBinding
import com.bibliotecadebolso.app.ui.appAccess.AppAccessViewModel

class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    private val appAccessViewModel: AppAccessViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root
    }

}
package com.bibliotecadebolso.app.ui.add.book

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentAddBookOptionsBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AddBookOptionsFragment : Fragment() {

    private var _binding: FragmentAddBookOptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddBookOptionsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddOwnBook.setOnClickListener {
            findNavController().navigate(R.id.action_optionsFragment_to_AddOfflineBookFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
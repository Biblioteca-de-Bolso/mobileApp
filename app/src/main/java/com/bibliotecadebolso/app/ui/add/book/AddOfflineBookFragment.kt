package com.bibliotecadebolso.app.ui.add.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.databinding.FragmentAddBookOfflineInputBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddOfflineBookFragment : Fragment() {

    private var _binding: FragmentAddBookOfflineInputBinding? = null
    private val viewModel: AddBookViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBookOfflineInputBinding.inflate(inflater, container, false)

        val btnAddBook = binding.btnAddBook

        btnAddBook.setOnClickListener {
            //Validation
            val isValid: Boolean = isInputsValid()

            if (isValid) {
                val title = binding.etBookTitle.editText!!.text.toString()
                val author = binding.etBookAuthor.editText!!.text.toString()
                val publisher = binding.etBookPublisher.editText!!.text.toString()
                val description = binding.etBookDescription.editText!!.text.toString()
                val prefs = requireActivity().getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    AppCompatActivity.MODE_PRIVATE
                )
                val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!

                binding.progressSending.visibility = View.VISIBLE
                viewModel.apiCreateBook(
                    accessToken = accessToken,
                    title = title,
                    author = author,
                    publisher = publisher,
                    description = description
                )
            }

            viewModel.isBookCreatedResponse.observe(viewLifecycleOwner) {
                binding.progressSending.visibility = View.GONE

                Toast.makeText(requireContext(), "Book Created", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }

        }
        return binding.root

    }

    private fun isInputsValid(): Boolean {
        val tilTitle = binding.etBookTitle
        val tilAuthor = binding.etBookAuthor
        val tilPublisher = binding.etBookPublisher
        val tilDescription = binding.etBookDescription

        if (tilTitle.editText!!.text.toString().length !in 1..128) {
            tilTitle.error = getString(R.string.error_must_be_beetween_1_128)
            return false
        }

        if (tilAuthor.editText!!.text.toString().length !in 0..128) {
            tilAuthor.error = getString(R.string.error_must_be_between_0_128)
            return false
        }

        if (tilPublisher.editText!!.text.toString().length !in 0..128) {
            tilPublisher.error = getString(R.string.error_must_be_between_0_128)
            return false
        }

        if (tilDescription.editText!!.text.toString().length !in 0..5000) {
            tilDescription.error = getString(R.string.error_must_be_between_0_5000)
            return false
        }

        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
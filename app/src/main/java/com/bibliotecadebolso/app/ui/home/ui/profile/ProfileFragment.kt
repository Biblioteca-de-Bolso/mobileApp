package com.bibliotecadebolso.app.ui.home.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.response.ProfileResponse
import com.bibliotecadebolso.app.databinding.FragmentProfileBinding
import com.bibliotecadebolso.app.ui.home.nav.DeleteAccountActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.llContent.visibility = View.INVISIBLE
        lifecycleScope.launch {
            getProfile()
        }

        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            binding.pgLoading.visibility = View.INVISIBLE
            binding.swlRefresh.isRefreshing = false
            when (it) {
                is Result.Success -> {
                    showContent()
                    fillFragment(it.response)
                }
                is Result.Error -> {
                    showErrorContent(it.errorBody.message)
                }
            }
        }

        binding.swlRefresh.setOnRefreshListener {
            getProfile()
        }

        binding.btnDeleteAccount.setOnClickListener {
            val intent = Intent(requireContext(), DeleteAccountActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun fillFragment(response: ProfileResponse) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        binding.apply {
            tvUsername.text = response.name
            tvEmail.text = response.email
            tvCreatedAt.text = getString(
                R.string.label_created_at,
                response.createdAt.format(formatter)
            )
            tvLastUpdatedAt.text = getString(
                R.string.label_updated_at,
                response.updatedAt.format(formatter)
            )
        }
    }

    private fun showContent() {
        hideErrorContent()
        binding.llContent.visibility = View.VISIBLE

    }

    private fun hideErrorContent() {
        binding.includeLlError.llErrorContent.visibility = View.GONE
    }

    private fun showErrorContent(message: String) {
        hideContent()
        binding.includeLlError.tvErrorTitle.text = message
        binding.includeLlError.llErrorContent.visibility = View.VISIBLE
    }

    private fun hideContent() {
        binding.llContent.visibility = View.GONE
    }

    private fun getProfile() {
        val prefs = requireActivity().getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            AppCompatActivity.MODE_PRIVATE
        )
        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!
        binding.pgLoading.visibility = View.VISIBLE
        viewModel.getProfile(accessToken)

    }
}
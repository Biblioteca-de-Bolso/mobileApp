package com.bibliotecadebolso.app.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import com.bibliotecadebolso.app.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)

        val data = intent.data

        val textViewRecoverCode = TextView(this)
        val textViewEmail = TextView(this)

        textViewRecoverCode.text = intent.data?.getQueryParameter("recoverCode") ?: "No recoverCode"
        textViewEmail.text = intent.data?.getQueryParameter("email") ?: "Email"

        binding.root.addView(textViewEmail)
        binding.root.addView(textViewRecoverCode)

        setContentView(binding.root)
    }
}
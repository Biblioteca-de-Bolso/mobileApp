package com.bibliotecadebolso.app.ui.borrow.add

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import com.bibliotecadebolso.app.databinding.ActivityAddBorrowBinding

class AddBorrowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBorrowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBorrowBinding.inflate(layoutInflater)

        binding.tilBorrowDate.editText?.keyListener = null;
        binding.tilBorrowDate.editText?.inputType = InputType.TYPE_NULL
        binding.tilBorrowDate.editText?.setOnFocusChangeListener { view, b ->
            if (b) {
                Log.e("test","hello")
                val datePickerDialog = DatePickerDialog(this)
                datePickerDialog.setOnDateSetListener { _, year, month, day ->
                    val yyyyMMdd = "$year/$month/$day"
                    binding.tilBorrowDate.editText?.setText(yyyyMMdd)
                }
                datePickerDialog.show()
            }
        }

        setContentView(binding.root)
    }
}
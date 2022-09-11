package com.bibliotecadebolso.app.ui.add.annotation.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions
import com.bibliotecadebolso.app.databinding.FragmentEditTransactionBinding
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorViewModel

class EditTransactionFragment : Fragment() {

    private lateinit var binding: FragmentEditTransactionBinding
    private val viewModel: AnnotationEditorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTransactionBinding.inflate(inflater, container, false)

        binding.apply {
            transformToBold.setOnClickListener {
                viewModel.transactionOptionSelected.postValue(TransactionOptions.TRANSFORM_BOLD)
            }
            transformToItalic.setOnClickListener {
                viewModel.transactionOptionSelected.postValue(TransactionOptions.TRANSFORM_ITALIC)
            }
            transformToNormal.setOnClickListener {
                viewModel.transactionOptionSelected.postValue(TransactionOptions.TRANSFORM_NORMAL)
            }
        }



        return binding.root
    }
}
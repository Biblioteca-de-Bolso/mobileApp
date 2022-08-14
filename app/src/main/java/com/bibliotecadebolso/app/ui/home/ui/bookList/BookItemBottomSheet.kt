package com.bibliotecadebolso.app.ui.home.ui.bookList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bibliotecadebolso.app.databinding.BookOptionsBottomSheetBinding
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BookItemBottomSheet(private val bookItemId: Int) : BottomSheetDialogFragment() {

    lateinit var binding: BookOptionsBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookOptionsBottomSheetBinding.inflate(inflater, container, false)

        binding.tvCreateAnnotation.setOnClickListener {
            val intent: Intent = Intent(this.requireContext(), AnnotationEditorActivity::class.java)
            intent.putExtra("bookId", bookItemId);
            startActivity(intent);
        }
        return binding.root
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}
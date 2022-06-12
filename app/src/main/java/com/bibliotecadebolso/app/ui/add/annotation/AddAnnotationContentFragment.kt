package com.bibliotecadebolso.app.ui.add.annotation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R

class AddAnnotationContentFragment : Fragment() {

    companion object {
        fun newInstance() = AddAnnotationContentFragment()
    }

    private lateinit var viewModel: AddAnnotationContentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_annotation_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddAnnotationContentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
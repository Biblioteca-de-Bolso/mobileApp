package com.bibliotecadebolso.app.ui.add.annotation

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding

class AddAnnotationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnotationBinding
    private lateinit var screenContent: LinearLayout
    private var focusedView: View? = null
    private var bookId: Int = -1;
    private var isActive: Boolean = false

    private lateinit var viewModel: AddAnnotationContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenContent = binding.llContent
        viewModel = ViewModelProvider(this)[AddAnnotationContentViewModel::class.java]

        val mEditor = binding.richEditor
        mEditor.setEditorHeight(200)
        mEditor.setEditorFontSize(16)
        mEditor.setBackgroundColor(Color.TRANSPARENT)
        mEditor.loadCSS("test.css")
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setPlaceholder(getString(R.string.annotation_placeholder_insert_text_here))

        binding.apply {
            actionBold.setOnClickListener { mEditor.setBold() }
            actionItalic.setOnClickListener { mEditor.setItalic() }
            actionStrikethrough.setOnClickListener { mEditor.setStrikeThrough() }
            actionUnderline.setOnClickListener { mEditor.setUnderline() }
            actionIndent.setOnClickListener { mEditor.setIndent() }
            actionOutdent.setOnClickListener { mEditor.setOutdent() }
            actionAlignLeft.setOnClickListener { mEditor.setAlignLeft() }
            actionAlignCenter.setOnClickListener { mEditor.setAlignCenter() }
            actionAlignRight.setOnClickListener { mEditor.setAlignRight() }
            actionBlockquote.setOnClickListener { mEditor.setBlockquote() }
            actionInsertBullets.setOnClickListener { mEditor.setBullets() }
            actionInsertNumbers.setOnClickListener { mEditor.setNumbers() }
            actionInsertCheckbox.setOnClickListener { mEditor.insertTodo() }
        }

    }

}
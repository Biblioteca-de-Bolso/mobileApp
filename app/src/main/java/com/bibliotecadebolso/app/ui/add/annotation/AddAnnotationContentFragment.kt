package com.bibliotecadebolso.app.ui.add.annotation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentAddAnnotationContentBinding


class AddAnnotationContentFragment : Fragment() {

    companion object {
        fun newInstance() = AddAnnotationContentFragment()
    }

    private lateinit var viewModel: AddAnnotationContentViewModel
    private lateinit var binding: FragmentAddAnnotationContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAnnotationContentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AddAnnotationContentViewModel::class.java)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        super.onCreate(savedInstanceState)
        val mEditor = binding.richEditor
        //mEditor.setEditorHeight(200)
        mEditor.setEditorFontSize(8)
        mEditor.setFontSize(8)
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setBackgroundResource(R.drawable.bg_annotation_content)
        mEditor.setBackgroundColor(Color.TRANSPARENT)
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...")
        //mEditor.setInputEnabled(false);

        //mEditor.setInputEnabled(false);
        // mPreview = findViewById(R.id.preview) as TextView
        // mEditor.setOnTextChangeListener(OnTextChangeListener { text -> mPreview.setText(text) })

        binding.apply {
            actionUndo.setOnClickListener { mEditor.undo() }
            actionRedo.setOnClickListener { mEditor.redo() }
            actionBold.setOnClickListener { mEditor.setBold() }
            actionItalic.setOnClickListener { mEditor.setItalic() }
            actionSubscript.setOnClickListener { mEditor.setSubscript() }
            actionSuperscript.setOnClickListener { mEditor.setSuperscript() }
            actionStrikethrough.setOnClickListener { mEditor.setStrikeThrough() }
            actionUnderline.setOnClickListener { mEditor.setUnderline() }
            actionHeading1.setOnClickListener { mEditor.setHeading(1) }
            actionHeading2.setOnClickListener { mEditor.setHeading(2) }
            actionHeading3.setOnClickListener { mEditor.setHeading(3) }
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
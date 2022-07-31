package com.bibliotecadebolso.app.ui.add.annotation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding
import jp.wasabeef.richeditor.RichEditor

class AddAnnotationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnotationBinding
    private lateinit var screenContent: LinearLayout
    private var focusedView: View? = null
    private var bookId: Int = -1;
    private var isActive: Boolean = false
    private lateinit var mPreview: TextView

    private lateinit var viewModel: AddAnnotationContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        screenContent = binding.llContent
        viewModel = ViewModelProvider(this)[AddAnnotationContentViewModel::class.java]


        val mEditor: RichEditor = binding.richEditor
        mEditor.setEditorHeight(200)
        mEditor.setEditorFontSize(16)
        mEditor.setBackgroundColor(Color.TRANSPARENT)
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
            actionInsertBullets.setOnClickListener { mEditor.setBullets() }
            actionInsertNumbers.setOnClickListener { mEditor.setNumbers() }
            actionHighlighterGreen.setOnClickListener(object : View.OnClickListener {
                var isChanged = false
                override fun onClick(v: View) {
                    if (isChanged) {
                        mEditor.evaluateJavascript("javascript:RE.prepareInsert();", null)
                        mEditor.evaluateJavascript("javascript:RE.removeBackgroundColor();", null)
                    } else {
                        mEditor.setTextBackgroundColor(Color.GREEN)
                    }

                    isChanged = !isChanged
                }
            })
        }

        binding.fabSaveAnnotation.setOnClickListener {
            val html: String = if (mEditor.html == null) "" else mEditor.html
            print(html)
            Log.i("mEditor", html)
        }

    }

}
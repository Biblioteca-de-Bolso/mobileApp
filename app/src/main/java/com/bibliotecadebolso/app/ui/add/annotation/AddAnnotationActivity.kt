package com.bibliotecadebolso.app.ui.add.annotation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.richeditor.RichEditor

class AddAnnotationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnotationBinding
    private var bookId: Int = -1;
    private lateinit var mEditor: RichEditor

    private lateinit var viewModel: AddAnnotationContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        assignGlobalVariables()
        finishIfBookIdNotValid()
        loadWebAnnotationView()
        setFabSaveAnnotationClick()
        setSaveAnnotationListener()
    }

    private fun assignGlobalVariables() {
        viewModel = ViewModelProvider(this)[AddAnnotationContentViewModel::class.java]
        mEditor = binding.richEditor
        bookId = getBookIdFromExtrasOrMinus1()
    }

    private fun getBookIdFromExtrasOrMinus1(): Int {
        val extras = intent.extras
        return extras?.getInt("bookId", -1) ?: -1
    }

    private fun finishIfBookIdNotValid() {
        if (bookId == -1) {
            Toast.makeText(this, "Book ID invalid", Toast.LENGTH_LONG).show()
            finish()

        }
    }

    private fun loadWebAnnotationView() {
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
                    Log.e("actionHighlighter", isChanged.toString())
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
    }

    private fun setFabSaveAnnotationClick() {
        binding.fabSaveAnnotation.setOnClickListener {
            val html: String = if (mEditor.html == null) "" else mEditor.html
            val title: String = binding.etBookTitle.editText!!.text.toString()
            val reference: String = binding.etBookReference.editText!!.text.toString()


            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )
            viewModel.saveAnnotation(accessToken, bookId, title, html, reference)
        }
    }

    private fun setSaveAnnotationListener() {
        viewModel.resultOfSaveAnnotation.observe(this) {
            if (it is Result.Success) {
                showSuccessfullyToastAndFinishActivity()
            } else if (it is Result.Error) {
                showSnackBar(it.errorBody.message)
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG).show()
    }

    private fun showSuccessfullyToastAndFinishActivity() {
        Toast.makeText(
            this,
            getString(R.string.label_annotation_saved_successfully),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

}
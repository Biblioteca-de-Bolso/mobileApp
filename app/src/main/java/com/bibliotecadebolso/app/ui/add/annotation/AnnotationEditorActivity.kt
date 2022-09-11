package com.bibliotecadebolso.app.ui.add.annotation

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.infix.changeHighlight
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.ContextUtils
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.richeditor.RichEditor

/**
 * Annotation edition activity.
 *     Send parameters through intent.putExtra()
 *
 * @param actionType it should be a AnnotationActionEnum type in a string format
 * @param bookId required only if actionType ie equals to 'add'
 * @param annotationId if actionType is equals to 'edit'
 */
class AnnotationEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnotationBinding
    private var bookId: Int = -1
    private lateinit var mEditor: RichEditor
    private var actionType: AnnotationActionEnum? = null
    private var annotationId: Int = -1
    private var isDarkMode = false

    private lateinit var viewModel: AnnotationEditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                isDarkMode = true
            }
        }
        ContextUtils.setActionBarColor(supportActionBar, this)

        assignGlobalVariables()
        finishIfExtrasIsNotValid()
        loadWebAnnotationView()
        loadContentToWebAnnotationView()
        setFabSaveAnnotationClick()
        setSaveAnnotationListener()
        setUpdateAnnotationListener()
        setGetAnnotationByIdListener()
    }


    private fun assignGlobalVariables() {
        viewModel = ViewModelProvider(this)[AnnotationEditorViewModel::class.java]
        mEditor = binding.richEditor

        val extras = intent.extras
        bookId = extras?.getInt("bookId", -1) ?: -1
        getActionTypeOrFinishActivity(extras)
        annotationId = extras?.getInt("annotationId", -1) ?: -1

    }

    private fun getActionTypeOrFinishActivity(extras: Bundle?) {
        try {
            val actionTypeString = extras?.getString("actionType", "") ?: ""
            actionType = AnnotationActionEnum.valueOf(actionTypeString)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, getString(R.string.label_action_not_valid), Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    private fun finishIfExtrasIsNotValid() {
        when (actionType) {
            AnnotationActionEnum.ADD ->
                if (bookId == -1) {
                    showLongToast(getString(R.string.label_book_id_not_valid))
                    finish()
                }
            AnnotationActionEnum.EDIT ->
                if (annotationId == -1) {
                    showLongToast(getString(R.string.label_annotation_id_not_valid))
                    finish()
                }
            else -> {}
        }
    }

    private fun loadWebAnnotationView() {
        mEditor.setEditorHeight(200)
        mEditor.setEditorFontSize(16)
        mEditor.setEditorFontColor(if (isDarkMode) Color.LTGRAY else Color.BLACK)
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setPlaceholder(getString(R.string.annotation_placeholder_insert_text_here))
        mEditor.setOnTextChangeListener {
            viewModel.changeHtmlBody(it)
        }

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
                var isToDisable = false
                override fun onClick(v: View) {
                    Log.e("actionHighlighter", isToDisable.toString())
                    mEditor.changeHighlight(isToDisable)
                    isToDisable = !isToDisable
                }
            })

            etBookTitle.editText?.doAfterTextChanged {
                it?.let { viewModel.changeTitle(it.toString()) }
            }
            etBookReference.editText?.doAfterTextChanged {
                it?.let { viewModel.changeReference(it.toString()) }
            }
        }
    }


    private fun loadContentToWebAnnotationView() {
        if (actionType == AnnotationActionEnum.ADD) {
            displayAnnotationContent()
        } else if (actionType == AnnotationActionEnum.EDIT) {
            if (viewModel.getByIdAlreadyLoaded) {
                displayAnnotationContent()
            } else {
                binding.progressSending.visibility = View.VISIBLE
                getAnnotationById()
            }
        }
    }

    private fun displayAnnotationContent() {
        viewModel.apply {
            mEditor.html = richEditorHtmlData
            binding.etBookTitle.editText?.setText(titleText)
            binding.etBookReference.editText?.setText(referenceText)
        }
    }


    private fun getAnnotationById() {
        viewModel.getAnnotationById(
            SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
            ),
            annotationId
        )
    }

    private fun setGetAnnotationByIdListener() {
        viewModel.getByIdResult.observe(this) {
            if (it is Result.Success && !viewModel.richEditorHtmlDataChanged) {
                val annotation = it.response.annotation
                viewModel.loadAnnotationContent(annotation)
                displayAnnotationContent()
                setSupportActionBarTitle(annotation.title)
            } else if (it is Result.Error) {
                showSnackBar(it.errorBody.message)
            }

            binding.progressSending.visibility = View.GONE
        }
    }

    private fun setSupportActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    private fun setFabSaveAnnotationClick() {
        binding.fabSaveAnnotation.setOnClickListener {
            val html: String = viewModel.richEditorHtmlData
            val title: String = viewModel.titleText
            val reference: String = viewModel.referenceText

            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
            )

            binding.progressSending.visibility = View.VISIBLE
            when (actionType) {
                AnnotationActionEnum.ADD -> viewModel.saveAnnotation(
                    accessToken,
                    bookId,
                    title,
                    html,
                    reference
                )
                AnnotationActionEnum.EDIT -> viewModel.updateAnnotation(
                    accessToken,
                    annotationId,
                    title,
                    html,
                    reference
                )
                else -> {}
            }

        }
    }

    private fun setSaveAnnotationListener() {
        viewModel.resultOfSaveAnnotation.observe(this) {
            if (it is Result.Success) {
                showSuccessfullyToastAndFinishActivity()
            } else if (it is Result.Error) {
                showSnackBar(it.errorBody.message)
            }

            binding.progressSending.visibility = View.GONE
        }
    }

    private fun setUpdateAnnotationListener() {
        viewModel.updateAnnotationResult.observe(this) {
            if (it is Result.Success) {
                showSuccessfullyToastAndFinishActivity()
            } else if (it is Result.Error) {
                showSnackBar(it.errorBody.message)
            }

            binding.progressSending.visibility = View.GONE
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

    private fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
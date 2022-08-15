package com.bibliotecadebolso.app.ui.add.annotation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
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

    private lateinit var viewModel: AddAnnotationContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        viewModel = ViewModelProvider(this)[AddAnnotationContentViewModel::class.java]
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


    private fun loadContentToWebAnnotationView() {
        if (actionType == AnnotationActionEnum.EDIT) {
            binding.progressSending.visibility = View.VISIBLE
            viewModel.getAnnotationById(
                SharedPreferencesUtils.getAccessToken(
                    getSharedPreferences(
                        Constants.Prefs.USER_TOKENS,
                        MODE_PRIVATE
                    )
                ),
                annotationId
            )
        }
    }

    private fun setGetAnnotationByIdListener() {
        viewModel.getByIdResult.observe(this) {
            if (it is Result.Success) {
                val annotation = it.response.annotation
                supportActionBar?.title = annotation.title
                mEditor.html = annotation.text
                binding.etBookTitle.editText?.setText(annotation.title)
                binding.etBookReference.editText?.setText(annotation.reference)
            } else if (it is Result.Error) {
                showSnackBar(it.errorBody.message)
            }

            binding.progressSending.visibility = View.GONE
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
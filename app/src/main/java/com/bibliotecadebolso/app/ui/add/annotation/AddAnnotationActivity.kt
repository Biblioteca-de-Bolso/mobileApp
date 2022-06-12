package com.bibliotecadebolso.app.ui.add.annotation

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.toHtml
import androidx.core.text.toSpannable
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions as TO
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding
import com.bibliotecadebolso.app.ui.add.annotation.transactions.EditTransactionFragment
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result

class AddAnnotationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAnnotationBinding
    private lateinit var screenContent: LinearLayout
    private var focusedView: View? = null
    private lateinit var editTransactionFragment: EditTransactionFragment
    private var bookId: Int = -1;
    private var isActive: Boolean = false

    private lateinit var viewModel: AddAnnotationContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAnnotationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAndCheckIfBookIdIsValid()

        screenContent = binding.llContent
        editTransactionFragment = EditTransactionFragment()
        viewModel = ViewModelProvider(this)[AddAnnotationContentViewModel::class.java]

        setFabTransactionListener()
        setFabSaveAnnotationListener()
        setTransactionObserver()
        setFabSaveAnnotationObserver()

        binding.etContent.onFocusChangeListener = textFocusListener
    }

    private fun getAndCheckIfBookIdIsValid() {
        intent.extras?.let {
            bookId = it.getInt("bookId", -1)
            if (bookId == -1) {
                Toast.makeText(this.baseContext, getString(R.string.label_book_not_valid), Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setFabTransactionListener() {
        binding.fabMakeTransaction.setOnClickListener {
            editTransactionFragment(
                type = if (isActive) "remove" else "add"
            )
            isActive = !isActive
        }
    }

    private fun setFabSaveAnnotationObserver() {
        viewModel.resultOfSaveAnnotation.observe(this) {
            if (it is Result.Success)
                Toast.makeText(this, "saved successfully", Toast.LENGTH_LONG).show()
            else if (it is Result.Error)
                Toast.makeText(this, "Error: ${it.errorBody.message}", Toast.LENGTH_LONG).show()

            finish()
        }
    }

    private fun setTransactionObserver() {
        viewModel.transactionOptionSelected.observe(this){
            val etFocused = (focusedView as EditText)
            val selectionStart = etFocused.selectionStart
            val selectionEnd = etFocused.selectionEnd

            val spannable: SpannableStringBuilder = etFocused.text as SpannableStringBuilder
            val styleSelected: StyleSpan = when (it) {
                TO.TRANSFORM_BOLD -> StyleSpan(Typeface.BOLD)
                TO.TRANSFORM_ITALIC -> StyleSpan(Typeface.ITALIC)
                TO.TRANSFORM_NORMAL -> StyleSpan(Typeface.NORMAL)
            }

            if (it.equals(TO.TRANSFORM_NORMAL)) {
                val spansToRemove = spannable.toSpannable().getSpans(selectionStart, selectionEnd, Any::class.java)
                for (span in spansToRemove) {
                    spannable.removeSpan(span)
                }

            } else
                spannable.setSpan(styleSelected, selectionStart, selectionEnd, 0)

            etFocused.setText(spannable.toSpannable(), TextView.BufferType.SPANNABLE)

        }
    }

    private fun setFabSaveAnnotationListener() {
        binding.fabSaveAnnotation.setOnClickListener {
            val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)

            val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!

            val content = binding.etContent.text as SpannableStringBuilder
            val title = binding.etBookTitle.editText?.text.toString()

            viewModel.saveAnnotation(accessToken, bookId, title, content.toHtml())
        }
    }

    private fun editTransactionFragment(type: String){
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.apply {
            if (type == "remove")
                remove(editTransactionFragment)
            else if (type == "add")
                add(binding.flTransactionContent.id, editTransactionFragment)
            commit()
        }
    }

    private val textFocusListener = View.OnFocusChangeListener { view, hasFocus ->
        if (hasFocus) focusedView = view
    }
}
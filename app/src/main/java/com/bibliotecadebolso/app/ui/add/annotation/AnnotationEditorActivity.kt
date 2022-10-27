package com.bibliotecadebolso.app.ui.add.annotation

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.databinding.ActivityAddAnnotationBinding
import com.bibliotecadebolso.app.infix.changeHighlight
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.ContextUtils
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private var topBarMenu: Menu? = null

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
        deleteAnnotationObserver()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (actionType == AnnotationActionEnum.ADD) return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.annotation_editor_menu, menu)

        topBarMenu = menu

        val iconExportToPdf = menu?.findItem(R.id.action_export_to_pdf)
        val iconDelete = menu?.findItem(R.id.action_delete)

        iconExportToPdf?.setOnMenuItemClickListener {
            requestStoragePermission()
            true
        }

        iconDelete?.setOnMenuItemClickListener {
            confirmToDeleteAnnotation(iconDelete)
            true
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun requestExportToPdf(menuItem: MenuItem?) {
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        )
        val request =
            DownloadManager.Request(Uri.parse("https://bibliotecadebolso.herokuapp.com/api/annotation/export/${annotationId}"))
                .addRequestHeader("Authorization", "Bearer $accessToken")

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(viewModel.titleText)
        request.setDescription(viewModel.referenceText)

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "${System.currentTimeMillis()}.pdf"
        )

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager


        val downloadID = manager.enqueue(request)
        disableMenuIcon(menuItem)

        val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadID == id) {
                    enableMenuIcon(menuItem)
                    unregisterReceiver(this)
                }
            }
        }

        registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        );
    }

    private fun requestStoragePermission() {
        val permsBelowAndroid11 = arrayOf("android.permission.WRITE_EXTERNAL_STORAGE")

        val perms = arrayOf("android.permission.READ_EXTERNAL_STORAGE")

        val permsRequestCodeBelowAndroid11 = 200
        val permsRequestCode = 201

        if (android.os.Build.VERSION.SDK_INT <= 29)
            requestPermissions(permsBelowAndroid11, permsRequestCodeBelowAndroid11)
        else
            requestPermissions(perms, permsRequestCode)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty()) return
        when (requestCode) {
            200 -> {
                val isReadPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (!isReadPermissionAccepted) {
                    Toast.makeText(this, getString(R.string.label_permission_not_granted), Toast.LENGTH_LONG).show()
                    return
                }

                val iconExportToPdf = topBarMenu?.findItem(R.id.action_export_to_pdf)
                requestExportToPdf(iconExportToPdf)
            }
            201 -> {
                val isReadPermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                if (!isReadPermissionAccepted) {
                    Toast.makeText(this, getString(R.string.label_permission_not_granted), Toast.LENGTH_LONG).show()
                    return
                }

                val iconExportToPdf = topBarMenu?.findItem(R.id.action_export_to_pdf)
                requestExportToPdf(iconExportToPdf)
            }
        }
    }

    private fun disableMenuIcon(menuItem: MenuItem?) {
        menuItem?.isEnabled = false
        menuItem?.iconTintList = ColorStateList.valueOf(Color.GRAY)
    }

    private fun enableMenuIcon(menuItem: MenuItem?) {
        menuItem?.isEnabled = true
        menuItem?.iconTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun confirmToDeleteAnnotation(menuItem: MenuItem?) =
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.label_are_you_sure))
            .setMessage(getString(R.string.label_are_you_sure_delete_annotation))
            .setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
            .setPositiveButton(resources.getString(R.string.label_delete)) { dialog, which ->
                disableMenuIcon(menuItem)
                deleteAnnotation()

            }
            .show()

    private fun deleteAnnotation() {
        viewModel.deleteAnnotation(
            SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
                ),
            annotationId
        )
    }

    private fun deleteAnnotationObserver() {
        viewModel.deleteAnnotationResult.observe(this) {
            when (it) {
                is Result.Success -> {
                    val returnResult = Intent()
                    returnResult.putExtra("id", annotationId)
                    setResult(REMOVE_ANNOTATION, returnResult)
                    finish()
                }
                is Result.Error -> {
                    enableMenuIcon(topBarMenu?.findItem(R.id.action_delete))
                }
            }
        }
    }

    companion object {
        const val REMOVE_ANNOTATION = 21
    }
}
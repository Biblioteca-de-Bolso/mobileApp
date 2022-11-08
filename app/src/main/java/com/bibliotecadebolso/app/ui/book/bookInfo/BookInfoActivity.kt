package com.bibliotecadebolso.app.ui.book.bookInfo

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.databinding.ActivityBookInfoBinding
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.bibliotecadebolso.app.ui.book.bookInfo.annotationList.AnnotationListActivity
import com.bibliotecadebolso.app.ui.book.edit.EditBookActivity
import com.bibliotecadebolso.app.ui.borrow.add.AddBorrowActivity
import com.bibliotecadebolso.app.ui.borrow.list.BorrowListActivity
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.*
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale


class BookInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        const val EDIT_BOOK = 21
    }

    private lateinit var binding: ActivityBookInfoBinding
    private lateinit var viewModel: BookInfoViewModel
    private lateinit var readingStatusAdapter: ArrayAdapter<CharSequence>
    private var userIsInteracting = false

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.fab_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.fab_close
        )
    }
    private val fabFromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.fab_from_bottom_anim
        )
    }
    private val fabToBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.fab_to_bottom_anim
        )
    }

    var isFabInvisible = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[BookInfoViewModel::class.java]
        setSupportActionBar(binding.toolbar)

        val bookId = getIdFromExtrasOrMinus1()
        finishActivityIfBookIdIsInvalid(bookId)
        setupReadingStatusSpinner()
        getBookById(bookId)
        listenerFillActivityWithBookInfo()

        setupFabs()
        setupBorrowDisponibilityListener()
        setupOnClickRemoveBook()
        setRemoveBookListener()
        setupOnClickEditBook()
        setOnClickEditImage()
        setupOnImageCompressedListener()
        setOnClickSaveNewImage()
        setOnImageUpdatedListener()
        setTvAnnotationShowMoreOnClickListener()
        setBorrowShowMoreOnClickListener()
        updateStatusListener()
    }

    private fun getIdFromExtrasOrMinus1(): Int = intent.extras?.getInt("id", -1) ?: -1

    private fun finishActivityIfBookIdIsInvalid(bookId: Any) {
        if (bookId == -1) {
            Toast.makeText(this, getString(R.string.label_book_not_found), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun getBookById(bookId: Int) {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)
        viewModel.getInfoByID(accessToken, bookId)
    }

    private fun setupFabs() {
        binding.fabShowAddOptions.setOnClickListener {
            setFabOptionsVisibility(!isFabInvisible)
            setFabAnimation(!isFabInvisible)
            isFabInvisible = !isFabInvisible
        }

        binding.fabAddAbstract.setOnClickListener {
            val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
            val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

            Toast.makeText(
                this,
                getString(R.string.label_analyzing_avaliability),
                Toast.LENGTH_LONG
            ).show()
            viewModel.checkIfCanBorrowBook(accessToken, getIdFromExtrasOrMinus1())
        }
        binding.fabAddAnnotation.setOnClickListener {
            val intent = Intent(this, AnnotationEditorActivity::class.java)
            intent.putExtra("bookId", getIdFromExtrasOrMinus1())
            intent.putExtra("actionType", AnnotationActionEnum.ADD.toString())
            startActivity(intent)
        }
    }

    private fun setupBorrowDisponibilityListener() {
        viewModel.liveDataPendingBorrowList.observe(this) {
            when (it) {
                is Result.Success -> {
                    if (it.response.isNotEmpty()) {
                        showLongToast(getString(R.string.label_book_borrowed_and_not_returned))
                    } else {
                        val intent = Intent(this, AddBorrowActivity::class.java)
                        intent.putExtra("bookId", getIdFromExtrasOrMinus1())
                        startActivity(intent)
                    }
                }
                is Result.Error -> {
                    showLongToast(it.errorBody.message)
                }
            }
        }
    }

    private fun setFabOptionsVisibility(isToBeVisible: Boolean) {
        val visibility = if (isToBeVisible) View.VISIBLE else View.GONE

        binding.fabAddAbstract.visibility = visibility
        binding.fabAddAnnotation.visibility = visibility
    }

    private fun setFabAnimation(isToStartAnimation: Boolean) {
        val miniFabAnimation = if (isToStartAnimation) fabFromBottom else fabToBottom
        val optionsFabAnimation = if (isToStartAnimation) rotateOpen else rotateClose

        binding.apply {
            fabShowAddOptions.startAnimation(optionsFabAnimation)
            fabAddAnnotation.startAnimation(miniFabAnimation)
            fabAddAbstract.startAnimation(miniFabAnimation)
        }
    }

    private fun setupOnClickRemoveBook() {
        binding.btnDeleteBook.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.label_are_you_sure))
                .setMessage(getString(R.string.label_are_you_sure_delete_book))
                .setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.label_delete)) { dialog, which ->
                    showLoadingBar()
                    deleteBook()
                }
                .show()
        }
    }

    private fun deleteBook() {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)
        binding.progressSending.visibility = View.VISIBLE
        viewModel.deleteBook(accessToken, getIdFromExtrasOrMinus1())
    }

    private fun setRemoveBookListener() {
        viewModel.bookResponses.deleteLiveData.observe(this) {
            if (it is Result.Success) {
                hideLoadingBar()
                showLongToast(getString(R.string.label_book_removed))

                val returnResult = Intent()
                returnResult.putExtra("id", getIdFromExtrasOrMinus1())

                val lastReadStatusEnum = viewModel.getLastReadStatusEnumOrNull()
                if (lastReadStatusEnum != null)
                    returnResult.putExtra("readStatusEnum", lastReadStatusEnum.toString())

                setResult(BookListFragment.REMOVE_BOOK, returnResult)
                finish()
            } else if (it is Result.Error) {
                showLongToast(it.errorBody.message)
            }
        }
    }

    private fun setupReadingStatusSpinner() {

        viewModel.setReadStatusValuesKeyMap(
            resources.getStringArray(R.array.spinner_book_reading_status_key),
            resources.getStringArray(R.array.spinner_book_reading_status_value)
        )
        binding.spinnerReadingStatus.onItemSelectedListener = this

        readingStatusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_book_reading_status_value,
            R.layout.spinner_item
        )

        with(readingStatusAdapter) {
            this.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerReadingStatus.adapter = this
        }

    }

    private fun listenerFillActivityWithBookInfo() {
        viewModel.bookResponses.generalLiveDataInfo.observe(this) {
            if (it is Result.Success)
                loadActivityWithBookInfo(it.response)
            else {
                showLongToast((it as Result.Error).errorBody.message)
                binding.btnDeleteBook.isEnabled = false
                binding.btnEditBook.isEnabled = false
                binding.spinnerReadingStatus.isEnabled = false
                finish()
            }
        }
    }

    private fun loadActivityWithBookInfo(book: Book) {
        binding.tvBookTitle.text = book.title
        binding.tvAuthor.text = book.author
        binding.tvIsbn10.text = "ISBN-10: ${book.isbn10}"
        binding.tvIsbn13.text = "ISBN-13: ${book.isbn13}"
        if (book.thumbnail.isNotEmpty())
            Glide.with(this).load(book.thumbnail)
                .centerInside()
                .into(binding.ivBookPreview)

        loadDescriptionContent(book)
        selectReadStatusOnSpinner(book.readStatus)
    }

    private fun loadDescriptionContent(book: Book) {
        val description = StringBuilder(book.description)

        binding.tvDescription.text = viewModel.getDescription(description)
        setTvDescriptionShowMore(description)

        binding.tvDescriptionShowMore.setOnClickListener {
            viewModel.states.isDescriptionShowMoreActive =
                !viewModel.states.isDescriptionShowMoreActive
            binding.tvDescription.text = viewModel.getDescription(description)
            changeTvDescriptionShowMore(viewModel.states.isDescriptionShowMoreActive)

        }
    }

    private fun setTvDescriptionShowMore(description: StringBuilder) {
        if (viewModel.isAShortDescription(description)) {
            binding.tvDescriptionShowMore.text = ""
            if (description.isEmpty())
                binding.tvDescription.text = getString(R.string.label_no_description)
        } else {
            changeTvDescriptionShowMore(viewModel.states.isDescriptionShowMoreActive)
        }
    }

    private fun changeTvDescriptionShowMore(descriptionShowMoreActive: Boolean) {
        binding.tvDescriptionShowMore.text =
            if (descriptionShowMoreActive) getString(R.string.label_show_less)
            else getString(R.string.label_show_more)
    }

    private fun selectReadStatusOnSpinner(readStatusEnum: ReadStatusEnum?) {
        if (readStatusEnum != null) {
            val indexOfReadingStatusLabel =
                viewModel.getReadStatusEnumIndexOnSpinner(readStatusEnum)
            val readingStatusKey = viewModel.getReadingStatusKeyByIndex(indexOfReadingStatusLabel)
            binding.spinnerReadingStatus.setSelection(
                readingStatusAdapter.getPosition(readingStatusKey)
            )
        }
    }


    private fun setupOnClickEditBook() {
        binding.btnEditBook.setOnClickListener {
            val intent = Intent(this, EditBookActivity::class.java)
            intent.putExtra("bookId", getIdFromExtrasOrMinus1().toLong())

            editBookActivityResult.launch(intent)
        }
    }

    private val editBookActivityResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == EDIT_BOOK) getBookById(getIdFromExtrasOrMinus1())
    }

    private fun setOnClickEditImage() {
        binding.ivBtnEditImage.setOnClickListener {
            if (shouldAskPermission()) {
                requestStoragePermission()
                return@setOnClickListener
            }
            launchImagePickerIntent()
        }
    }

    private fun launchImagePickerIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        imagePickerActivityResult.launch(photoPickerIntent)
    }

    private fun requestStoragePermission() {
        val perms = arrayOf("android.permission.READ_EXTERNAL_STORAGE")

        val permsRequestCode = 200

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
                    Toast.makeText(
                        this,
                        getString(R.string.label_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                launchImagePickerIntent()


            }
        }
    }

    private var imagePickerActivityResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result != null) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                val realURI = URIUtils.getRealPathFromURIForGallery(imageUri, this)
                realURI?.let { viewModel.compressImage(this, it) }
                showLongToast(getString(R.string.label_compressing_image))
                showLoadingBar()
            }
        }
    }

    private fun setupOnImageCompressedListener() {
        viewModel.liveDataImageCompressed.observe(this) {
            val newBitmap = BitmapFactory.decodeFile(it.path)
            Glide.with(this@BookInfoActivity)
                .load(newBitmap)
                .into(binding.ivBookPreview)

            binding.ivBtnSaveImage.visibility = View.VISIBLE
            hideLoadingBar()
            showLongToast(getString(R.string.label_image_compressed))
        }
    }


    private fun setOnClickSaveNewImage() {
        binding.ivBtnSaveImage.setOnClickListener {
            if (WifiService.instance.isNotOnline()) {
                showLongToast(getString(R.string.label_no_internet_connection))
                return@setOnClickListener
            }
            val imageFile = viewModel.liveDataImageCompressed.value
            if (imageFile == null) {
                showLongToast(getString(R.string.label_theres_not_an_image_to_update))
                return@setOnClickListener
            }
            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )
            viewModel.updateImageBookById(this, accessToken, getIdFromExtrasOrMinus1(), imageFile)
            showLoadingBar()
            binding.ivBtnSaveImage.visibility = View.GONE
        }
    }

    private fun setOnImageUpdatedListener() {
        viewModel.bookResponses.liveDataUpdateImage.observe(this) {
            hideLoadingBar()
            if (it is Result.Error) {
                showLongToast(it.errorBody.message)
                binding.ivBtnSaveImage.visibility = View.VISIBLE
            } else {
                showLongToast((it as Result.Success).response)
                viewModel.states.updatedBookImage = true
            }
        }
    }

    private fun setTvAnnotationShowMoreOnClickListener() {
        binding.ivIcAnnotation1.setOnClickListener(annotationListOnClickListener)
        binding.tvAnnotationShowMore.setOnClickListener(annotationListOnClickListener)
    }

    private val annotationListOnClickListener = View.OnClickListener {
        val intent = Intent(this@BookInfoActivity, AnnotationListActivity::class.java)
        intent.putExtra("bookId", getIdFromExtrasOrMinus1())
        startActivity(intent)
    }

    private fun setBorrowShowMoreOnClickListener() {
        binding.ivIcBorrow.setOnClickListener(borrowListOnClickListener)
        binding.tvBorrowShowMore.setOnClickListener(borrowListOnClickListener)
    }

    private val borrowListOnClickListener = View.OnClickListener {
        val intent = Intent(this@BookInfoActivity, BorrowListActivity::class.java)
        intent.putExtra("bookId", getIdFromExtrasOrMinus1())
        startActivity(intent)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (!userIsInteracting) return
        val itemLabelSelected: String = p0?.getItemAtPosition(p2).toString()
        val readingStatusSelected: ReadStatusEnum =
            getReadingStatusOrSetAsPlanning(itemLabelSelected)

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.label_are_you_sure))
            .setMessage("Are you sure you want to change the book status? New status: $itemLabelSelected")
            .setNegativeButton(getString(R.string.label_cancel)) { _, _ ->
            }
            .setPositiveButton(resources.getString(R.string.label_update)) { dialog, which ->
                updateStatus(readingStatusSelected)
            }
            .show()
    }

    private fun getReadingStatusOrSetAsPlanning(itemLabelSelected: String): ReadStatusEnum {
        return viewModel.readingStatusValuesKey[itemLabelSelected]?.let { readStatusString: String ->
            try {
                ReadStatusEnum.valueOf(readStatusString)
            } catch (e: IllegalArgumentException) {
                ReadStatusEnum.PLANNING
            }
        } ?: ReadStatusEnum.PLANNING
    }


    private fun updateStatus(readingStatusSelected: ReadStatusEnum) {

        val bookNullWithStatusOnly = UpdateBook(
            bookId = getIdFromExtrasOrMinus1().toLong(),
            readStatus = readingStatusSelected
        )
        binding.spinnerReadingStatus.isClickable = false
        binding.progressSending.visibility = View.VISIBLE
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )
        viewModel.updateBookByPatch(accessToken, bookNullWithStatusOnly)
    }

    private fun updateStatusListener() {
        viewModel.bookResponses.updateStatusLiveData.observe(this) {
            binding.spinnerReadingStatus.isClickable = true
            hideLoadingBar()
            if (it is Result.Success) {
                viewModel.states.updatedStatus = true
                Snackbar.make(
                    binding.root,
                    getString(R.string.label_book_status_updated),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLongToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun hideLoadingBar() {
        binding.progressSending.visibility = View.GONE
    }

    private fun showLoadingBar() {
        binding.progressSending.visibility = View.VISIBLE
    }

    private fun shouldAskPermission(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }


    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Glide.with(this).clear(binding.ivBookPreview)

        val returnResult = Intent()
        val returnStatus =
            if (viewModel.states.updatedBookImage && viewModel.states.updatedStatus)
                BookListFragment.UPDATED_BOOK_AND_STATUS
            else if (viewModel.states.updatedStatus) BookListFragment.UPDATED_STATUS
            else if (viewModel.states.updatedBookImage) BookListFragment.UPDATED_BOOK
            else null

        returnResult.putExtra("id", getIdFromExtrasOrMinus1())

        if (returnStatus == BookListFragment.UPDATED_STATUS || returnStatus == BookListFragment.UPDATED_BOOK_AND_STATUS) {
            returnResult.putExtra("newStatus", viewModel.getLastReadStatusEnumOrNull())
        }


        val lastReadStatusEnum = viewModel.getLastReadStatusEnumOrNull()
        if (lastReadStatusEnum != null)
            returnResult.putExtra("readStatusEnum", lastReadStatusEnum.toString())

        setResult(BookListFragment.REMOVE_BOOK, returnResult)
        finish()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userIsInteracting = true
    }
}
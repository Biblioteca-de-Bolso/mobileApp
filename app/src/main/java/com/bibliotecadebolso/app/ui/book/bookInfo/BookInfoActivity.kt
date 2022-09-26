package com.bibliotecadebolso.app.ui.book.bookInfo

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bibliotecadebolso.app.util.WifiService
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class BookInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

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
        setupOnClickRemoveBook()
        setRemoveBookListener()
        setupOnClickEditBook()
        setOnClickEditImage()
        setupOnImageCompressedListener()
        setOnClickSaveNewImage()
        setOnImageUpdatedListener()
        setTvAnnotationShowMoreOnClickListener()
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
            Toast.makeText(this, "fabAddAbstract clicked", Toast.LENGTH_LONG).show()
        }
        binding.fabAddAnnotation.setOnClickListener {
            val intent = Intent(this, AnnotationEditorActivity::class.java)
            intent.putExtra("bookId", getIdFromExtrasOrMinus1())
            intent.putExtra("actionType", AnnotationActionEnum.ADD.toString())
            startActivity(intent)
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
        viewModel.liveDataDeleteBook.observe(this) {
            if (it is Result.Success) {
                hideLoadingBar()
                showLongToast(getString(R.string.label_book_removed))

                val returnResult = Intent()
                returnResult.putExtra("id", getIdFromExtrasOrMinus1())

                val lastReadStatusEnum =
                    if (viewModel.liveDataUpdateBook.value != null &&
                        viewModel.liveDataUpdateBook.value is Result.Success)
                        (viewModel.liveDataUpdateBook.value as Result.Success).response.readStatus
                else (viewModel.liveDataBookInfo.value as Result.Success).response.readStatus

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
        viewModel.liveDataBookInfo.observe(this) {
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
            viewModel.isDescriptionShowMoreActive = !viewModel.isDescriptionShowMoreActive
            binding.tvDescription.text = viewModel.getDescription(description)
            changeTvDescriptionShowMore(viewModel.isDescriptionShowMoreActive)

        }
    }

    private fun setTvDescriptionShowMore(description: StringBuilder) {
        if (viewModel.isAShortDescription(description)) {
            binding.tvDescriptionShowMore.text = ""
            if (description.isEmpty())
                binding.tvDescription.text = getString(R.string.label_no_description)
        } else {
            changeTvDescriptionShowMore(viewModel.isDescriptionShowMoreActive)
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

            startActivity(intent)
        }
    }


    private fun setOnClickEditImage() {
        binding.ivBtnEditImage.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imagePickerActivityResult.launch(photoPickerIntent)
        }
    }

    private var imagePickerActivityResult = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result != null) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                val realURI = getRealPathFromURIForGallery(imageUri)
                realURI?.let { viewModel.compressImage(this, it) }
                showLongToast(getString(R.string.label_compressing_image))
                showLoadingBar()
            }
        }
    }

    private fun getRealPathFromURIForGallery(uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = this.contentResolver.query(
            uri, projection, null,
            null, null
        )
        if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        assert(false)
        cursor?.close()
        return uri.path
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
        viewModel.liveDataUpdateImage.observe(this) {
            hideLoadingBar()
            if (it is Result.Error) {
                showLongToast(it.errorBody.message)
                binding.ivBtnSaveImage.visibility = View.VISIBLE
            } else
                showLongToast((it as Result.Success).response)
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
        viewModel.liveDataUpdateBook.observe(this) {
            binding.spinnerReadingStatus.isClickable = true
            hideLoadingBar()
            if (it is Result.Success) {
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


    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Glide.with(this).clear(binding.ivBookPreview)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userIsInteracting = true
    }
}
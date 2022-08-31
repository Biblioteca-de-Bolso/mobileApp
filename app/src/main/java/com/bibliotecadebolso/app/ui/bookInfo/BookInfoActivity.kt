package com.bibliotecadebolso.app.ui.bookInfo

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.databinding.ActivityBookInfoBinding
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.bibliotecadebolso.app.ui.bookInfo.annotationList.AnnotationListActivity
import com.bibliotecadebolso.app.ui.edit.book.EditBookActivity
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.lang.Math.log10
import java.text.DecimalFormat
import kotlin.math.pow


class BookInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityBookInfoBinding
    private lateinit var viewModel: BookInfoViewModel
    private val readingStatusValuesKey = HashMap<String, String>()
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

        binding.tvAnnotationShowMore.setOnClickListener {
            val intent = Intent(this, AnnotationListActivity::class.java)
            intent.putExtra("bookId", getIdFromExtrasOrMinus1())
            startActivity(intent)
        }
    }

    private fun getBookById(bookId: Int) {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)
        viewModel.getInfoByID(accessToken, bookId)
    }

    private fun setupOnClickRemoveBook() {
        binding.btnDeleteBook.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.label_are_you_sure))
                .setMessage(getString(R.string.label_are_you_sure_delete_book))
                .setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.label_delete)) { dialog, which ->
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
                binding.progressSending.visibility = View.GONE
                Toast.makeText(this, getString(R.string.label_book_removed), Toast.LENGTH_SHORT)
                    .show()
                val returnResult = Intent()
                setResult(BookListFragment.REMOVE_BOOK, returnResult)
                finish()
            } else if (it is Result.Error) {
                Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
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

    private fun setFabAnimation(isToStartAnimation: Boolean) {
        val miniFabAnimation = if (isToStartAnimation) fabFromBottom else fabToBottom
        val optionsFabAnimation = if (isToStartAnimation) rotateOpen else rotateClose

        binding.apply {
            fabShowAddOptions.startAnimation(optionsFabAnimation)
            fabAddAnnotation.startAnimation(miniFabAnimation)
            fabAddAbstract.startAnimation(miniFabAnimation)
        }
    }

    private fun setFabOptionsVisibility(isToBeVisible: Boolean) {
        val visibility = if (isToBeVisible) View.VISIBLE else View.GONE

        binding.fabAddAbstract.visibility = visibility
        binding.fabAddAnnotation.visibility = visibility
    }

    private fun getIdFromExtrasOrMinus1(): Int {
        val extras = intent.extras
        return extras?.getInt("id", -1) ?: -1
    }

    private fun finishActivityIfBookIdIsInvalid(bookId: Any) {
        if (bookId == -1) {
            Toast.makeText(this, getString(R.string.label_book_not_found), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupReadingStatusSpinner() {
        val spinnerReadingStatusKey =
            resources.getStringArray(R.array.spinner_book_reading_status_key)
        val spinnerReadingStatusValue =
            resources.getStringArray(R.array.spinner_book_reading_status_value)


        for (i in spinnerReadingStatusKey.indices) {
            readingStatusValuesKey[spinnerReadingStatusValue[i]] = spinnerReadingStatusKey[i]
        }
        binding.spinnerReadingStatus.onItemSelectedListener = this

        readingStatusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_book_reading_status_value,
            R.layout.spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerReadingStatus.adapter = arrayAdapter
        }

    }


    private fun listenerFillActivityWithBookInfo() {
        viewModel.liveDataBookInfo.observe(this) {
            if (it is Result.Success) {
                loadActivityWithBookInfo(it.response)

            }
        }
    }

    private fun loadActivityWithBookInfo(book: Book) {
        binding.tvBookTitle.text = book.title
        binding.tvAuthor.text = book.author
        if (book.thumbnail.isNotEmpty())
            Glide.with(this).load(book.thumbnail)
                .centerInside()
                .into(binding.ivBookPreview)

        loadDescriptionContent(book)
        if (book.readStatusEnum != null && book.readStatusEnum != ReadStatusEnum.NO_STATUS) {

            val indexOfReadingStatusLabel = readingStatusValuesKey.values.toList()
                .indexOf(
                    book.readStatusEnum.toString()
                )
            // valor PT => valor PT para
            binding.spinnerReadingStatus.setSelection(
                readingStatusAdapter.getPosition(
                    readingStatusValuesKey.keys.toList()[indexOfReadingStatusLabel]
                )
            )
        }
    }

    private fun loadDescriptionContent(book: Book) {
        val description = StringBuilder(book.description)
        val isAShortDescription = description.length <= 270

        if (viewModel.isDescriptionShowMoreActive)
            setFullDescription(description)
        else {
            if (isAShortDescription) {
                setFullDescription(description)
                disableTvDescriptionShowMore()
            } else
                setShortDescription(description)
        }



        binding.tvDescriptionShowMore.setOnClickListener {
            if (viewModel.isDescriptionShowMoreActive)
                setShortDescription(description)
            else
                setFullDescription(description)

            viewModel.isDescriptionShowMoreActive = !viewModel.isDescriptionShowMoreActive
        }
    }

    private fun setFullDescription(description: StringBuilder) {
        binding.tvDescription.text = description.toString()
        if (description.length > 270)
            binding.tvDescriptionShowMore.text = getString(R.string.label_show_less)
    }

    private fun disableTvDescriptionShowMore() {
        binding.tvDescriptionShowMore.visibility = View.INVISIBLE
    }

    private fun setShortDescription(description: StringBuilder) {
        if (description.length > 270) {
            val shortDescription = description.substring(0, 270) + "..."
            binding.tvDescription.text = shortDescription
            binding.tvDescriptionShowMore.text = getString(R.string.label_show_more)
        }
    }

    private fun setupOnClickEditBook() {
        binding.btnEditBook.setOnClickListener {
            val intent = Intent(this, EditBookActivity::class.java)
            intent.putExtra("bookId", getIdFromExtrasOrMinus1().toLong())

            startActivity(intent)
        }
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (!userIsInteracting) return
        val itemLabelSelected: String = p0?.getItemAtPosition(p2).toString()
        val readingStatusSelected: ReadStatusEnum =
            getReadingStatusOrSetAsNoStatus(itemLabelSelected)

        if (readingStatusSelected != ReadStatusEnum.NO_STATUS)
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.label_are_you_sure))
                .setMessage("Are you sure you want to change the book status? New status: $itemLabelSelected")
                .setNegativeButton(getString(R.string.label_cancel)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.label_update)) { dialog, which ->
                    updateStatus(getIdFromExtrasOrMinus1(), readingStatusSelected)
                }
                .show()
    }

    private fun getReadingStatusOrSetAsNoStatus(itemLabelSelected: String): ReadStatusEnum {
        return readingStatusValuesKey[itemLabelSelected]?.let { readStatusString: String ->
            try {
                ReadStatusEnum.valueOf(readStatusString)
            } catch (e: IllegalArgumentException) {
                ReadStatusEnum.NO_STATUS
            }
        } ?: ReadStatusEnum.NO_STATUS
    }


    private fun updateStatus(idFromExtrasOrMinus1: Int, readingStatusSelected: ReadStatusEnum) {
        val book = (viewModel.liveDataBookInfo.value!! as Result.Success).response
        // val updateBook = with(book) { UpdateBook(idFromExtrasOrMinus1.toLong(), title, author,)}
        val bookChanged = book.copy(readStatusEnum = readingStatusSelected)
        binding.progressSending.visibility = View.VISIBLE
        viewModel.liveDataUpdateBook.observe(this) {
            binding.progressSending.visibility = View.GONE
            if (it is Result.Success) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.label_book_status_updated),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )
        // viewModel.updateBook(accessToken, bookChanged)
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
                Log.e("realURI", realURI.toString())
                realURI?.let { viewModel.compressImage(this, it) }
                Toast.makeText(
                    this,
                    getString(R.string.label_compressing_image),
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressSending.visibility = View.VISIBLE
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

            Log.e("compressedImageSize", viewModel.getReadableFileSize(it.length()))

            binding.ivBtnSaveImage.visibility = View.VISIBLE
            binding.progressSending.visibility = View.GONE

            Toast.makeText(this, getString(R.string.label_image_compressed), Toast.LENGTH_SHORT)
                .show()
        }
    }



    private fun setOnClickSaveNewImage() {
        binding.ivBtnSaveImage.setOnClickListener {
            val imageFile = viewModel.liveDataImageCompressed.value
            if (imageFile == null) {
                Toast.makeText(
                    this,
                    getString(R.string.label_theres_not_an_image_to_update),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )
            viewModel.updateImageBookById(this, accessToken, getIdFromExtrasOrMinus1(), imageFile)
            binding.progressSending.visibility = View.VISIBLE
            binding.ivBtnSaveImage.visibility = View.GONE


        }
    }

    private fun setOnImageUpdatedListener() {
        viewModel.liveDataUpdateImage.observe(this) {
            Log.e("imageUpdatedListener", it.toString())

            binding.progressSending.visibility = View.GONE
            if (it is Result.Error) {
                Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, (it as Result.Success).response, Toast.LENGTH_SHORT).show()
            }
        }
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
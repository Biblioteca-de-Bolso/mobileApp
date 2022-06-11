package com.bibliotecadebolso.app.ui.add.annotation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions

class AddAnnotationContentViewModel : ViewModel() {

    val transactionOptionSelected = MutableLiveData<TransactionOptions>()
}
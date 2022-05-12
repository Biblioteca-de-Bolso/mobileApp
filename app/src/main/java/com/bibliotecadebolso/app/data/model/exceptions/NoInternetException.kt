package com.bibliotecadebolso.app.data.model.exceptions

import okio.IOException

class NoInternetException(override val message: String) : IOException(message) {

}
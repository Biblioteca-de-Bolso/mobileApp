package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.response.BookResponse
import com.bibliotecadebolso.app.data.validator.FieldValidation
import com.bibliotecadebolso.app.data.validator.ValidationError

class BookValidation(val book: BookResponse) : FieldValidation {
    override fun validate(): ValidationError? {
        if (!isTitleValid(book.title))
            return ValidationError("title", "title length must be between 1 and 128")
        if (!isAuthorNameValid(book.author))
            return ValidationError("author", "Author length must be between 0 and 128")

        if (!isPublisherNameValid(book.publisher))
            return ValidationError("publisher", "Publisher must be between 0 and 128")

        if (!isDescriptionValid(book.description))
            return ValidationError("description", "Description must be between 0 and 128")

        return null

    }

    private fun isTitleValid(title: String) = title.length in 1..128

    private fun isAuthorNameValid(authorName: String) = authorName.length in 0..128

    private fun isPublisherNameValid(publisherName: String) = publisherName.length in 0..128

    private fun isDescriptionValid(description: String) = description.length in 0..5000
}
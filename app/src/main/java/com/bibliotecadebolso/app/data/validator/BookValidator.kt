package com.bibliotecadebolso.app.data.validator

object BookValidator {

    fun isTitleValid(title: String): Boolean {
        return title.length in 1..128
    }

    fun isAuthorNameValid(authorName: String): Boolean {
        return authorName.length in 0..128
    }

    fun isPublisherNameValid(publisherName: String): Boolean {
        return publisherName.length in 0..128
    }

    fun isDescriptionValid(description: String): Boolean {
        return description.length in 0..128
    }
}
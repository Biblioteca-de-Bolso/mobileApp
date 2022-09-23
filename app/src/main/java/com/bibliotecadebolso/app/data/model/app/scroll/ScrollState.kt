package com.bibliotecadebolso.app.data.model.app.scroll

open class ScrollState(
    var isLoadingNewItems: Boolean = false,
    var scrollOnBottom: Boolean = false,
) {
    fun setAllBooleanAs(booleanState: Boolean) {
        isLoadingNewItems = booleanState
        scrollOnBottom = booleanState
    }
}
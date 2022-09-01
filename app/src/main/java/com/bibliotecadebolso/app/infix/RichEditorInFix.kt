package com.bibliotecadebolso.app.infix

import android.graphics.Color
import com.bibliotecadebolso.app.R
import jp.wasabeef.richeditor.RichEditor

infix fun RichEditor.changeHighlight(isToDisable: Boolean) {
    if (isToDisable) {
        this.evaluateJavascript("javascript:RE.prepareInsert();", null)
        this.evaluateJavascript("javascript:RE.removeBackgroundColor();", null)
    } else {
        this.setTextBackgroundColor(resources.getColor(R.color.highlight_purple))
    }
}
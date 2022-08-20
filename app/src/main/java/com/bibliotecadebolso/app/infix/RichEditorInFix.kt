package com.bibliotecadebolso.app.infix

import android.graphics.Color
import jp.wasabeef.richeditor.RichEditor

infix fun RichEditor.changeHighlight(isToDisable: Boolean) {
    if (isToDisable) {
        this.evaluateJavascript("javascript:RE.prepareInsert();", null)
        this.evaluateJavascript("javascript:RE.removeBackgroundColor();", null)
    } else {
        this.setTextBackgroundColor(Color.GREEN)
    }
}
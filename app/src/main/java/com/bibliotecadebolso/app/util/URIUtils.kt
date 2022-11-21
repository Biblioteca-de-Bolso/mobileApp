package com.bibliotecadebolso.app.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

object URIUtils {

    fun getRealPathFromURIForGallery(uri: Uri?, context: Context): String? {
        if (uri == null) {
            return null
        }
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(
            uri, projection, null,
            null, null
        )
        if (cursor != null) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }

        assert(false)
        return uri.path
    }

}
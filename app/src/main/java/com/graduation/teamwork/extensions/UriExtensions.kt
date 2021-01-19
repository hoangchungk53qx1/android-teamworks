package com.graduation.teamwork.extensions

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

fun Uri.getRealPathFromURI(context: Context): String? {
    val loader = CursorLoader(
        context,
        this,
        arrayOf(MediaStore.Images.Media.DATA),
        null,
        null,
        null
    )

    val cursor: Cursor? = loader.loadInBackground()
    val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    val result: String? = columnIndex?.let { cursor.getString(it) }
    cursor?.close()
    return result
}
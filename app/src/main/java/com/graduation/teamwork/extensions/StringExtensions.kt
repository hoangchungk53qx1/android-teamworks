package com.graduation.teamwork.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.text.Normalizer


val normalizeRegex = "\\p{InCombiningDiacriticalMarks}+".toRegex()

/**
 * Strip the accents from a string
 */
fun CharSequence.removeAccents(): String = Normalizer.normalize(this, Normalizer.Form.NFKD).replace(
    Regex(
        "\\p{M}"
    ), ""
)

// if we are comparing phone numbers, compare just the last 9 digits
fun String.trimToComparableNumber(): String {
    val normalizedNumber = this.normalizeString()
    val startIndex = Math.max(0, normalizedNumber.length - 9)
    return normalizedNumber.substring(startIndex)
}

// remove diacritics, for example č -> c
fun String.normalizeString() = Normalizer.normalize(this, Normalizer.Form.NFD).replace(
    normalizeRegex, ""
)

// use glide
//Glide.with(CaptchaFragment.this).load(decodedBytes).crossFade().fitCenter().into(mCatpchaImageView);
fun String.toByte(): Bitmap {
    val decodedString: ByteArray = Base64.decode(this, Base64.DEFAULT)
    
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}
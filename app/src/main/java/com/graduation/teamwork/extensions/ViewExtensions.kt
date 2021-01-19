package com.graduation.teamwork.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * com.graduation.teamwork.extensions
 * Created on 11/8/20
 */

fun View.setVisiable(visiable: Boolean, invisiable: Int = View.GONE) {
    visibility = if (visiable) View.VISIBLE else invisiable
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visiable() {
    visibility = View.VISIBLE
}

fun View.invisiable() {
    visibility = View.INVISIBLE
}

fun View.toggleViewActivation() {
    isActivated = !isActivated
}

/**
 * Editext
 */

fun EditText.showKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
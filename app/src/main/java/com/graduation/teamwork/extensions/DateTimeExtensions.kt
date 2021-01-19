package com.graduation.teamwork.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ConstantLocale")
val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@SuppressLint("ConstantLocale")
val formatDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

@SuppressLint("ConstantLocale")
val formatTime = SimpleDateFormat("hh:mm", Locale.getDefault())

fun Date.formatDate() = formatDate.format(this)

fun Date.formatDateTime() = formatDateTime.format(this)

fun Date.formatTime() = formatTime.format(this)

fun Date.isSameDay(other: Date): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = other

    return cal1.isSameDay(cal2)
}

fun Date.sameDay(timestamp: Long): Boolean {
    return this.isSameDay(Date(timestamp))
}

fun Date.sameDay(date: String): Boolean {
    return this.isSameDay(Date(date))
}

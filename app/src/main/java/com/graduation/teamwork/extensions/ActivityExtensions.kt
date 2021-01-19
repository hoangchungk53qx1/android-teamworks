package com.graduation.teamwork.extensions

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * com.graduation.teamwork.extensions
 * Created on 11/8/20
 */


// one line binding
// ex:     private val binding by viewBinding(MainActivityBinding::inflate)
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
	crossinline bindingInflater: (LayoutInflater) -> T
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
	bindingInflater(layoutInflater)
}
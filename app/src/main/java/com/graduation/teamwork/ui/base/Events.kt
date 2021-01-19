package com.graduation.teamwork.ui.base

/**
 * Abstract Event from ViewModel
 */
open class ViewModelEvent

/**
 * Generic Pending Event
 */
object Pending : ViewModelEvent()

/**
 * Generic Success Event
 */
data class Success(val data: String? = null) : ViewModelEvent()

/**
 * Generic Failed Event
 */
data class Error(val tag: String, val error: Throwable) : ViewModelEvent()
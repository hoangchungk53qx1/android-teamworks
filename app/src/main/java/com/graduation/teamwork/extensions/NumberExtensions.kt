package com.graduation.teamwork.extensions

import java.util.*

/**
 * com.graduation.teamwork.extensions
 * Created on 11/8/20
 */

// convert second to hh:mm:ss
fun Int.duration(): String {
	when {
		this > 3600 -> {
			var timeSecond = this
			val h = timeSecond / 3600
			timeSecond %= 3600
			val m = timeSecond / 60
			timeSecond %= 60
			
			return "${h}h${m}m${timeSecond}s"
		}
		this > 60 -> {
			var timeSecond = this
			val m = timeSecond / 60
			timeSecond %= 60
			
			return "${m}m${timeSecond}s"
		}
		else -> return "${this}s"
	}
}

fun now(): Long {
	return System.currentTimeMillis()
}

fun Long.toDate() = Date(this)

fun Long.now() = System.currentTimeMillis()


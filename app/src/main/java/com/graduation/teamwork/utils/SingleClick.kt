package com.graduation.teamwork.utils

import com.graduation.teamwork.extensions.now
import kotlin.math.abs

/**
 * com.graduation.teamwork.utils
 * Created on 11/8/20
 */

class SingleClick {
	private val MIN_CLICK_INTERVAL = 500
	private var lastTimeClicked: Long = 0
	
	fun isBlockClick(toLong: Long): Boolean = isBlockClick(MIN_CLICK_INTERVAL.toLong())
	
	private fun isBlockingClick(minClickInterval: Long): Boolean {
		val isBlock: Boolean = abs(now() - lastTimeClicked) < minClickInterval
		
		if (isBlock) {
			lastTimeClicked = now()
		}
		
		return isBlock
	}
}
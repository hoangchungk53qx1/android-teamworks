package com.graduation.teamwork.utils.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import com.graduation.teamwork.extensions.gone
import com.graduation.teamwork.extensions.visiable


/**
 * com.graduation.teamwork.utils.animation
 * Created on 11/14/20
 */

object ViewAnimation {
    fun rotateFab(v: View, isRotate: Boolean): Boolean {

        v.animate().setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {})
            .rotation(if (isRotate) 135f else 0f)

        return isRotate
    }

    fun init(v: View) {
        with(v) {
            gone()
            translationY = v.height.toFloat()
            alpha = 0f
        }
    }

    fun showAnimation(v: View, isShow: Boolean) {
        if (isShow) {
            showIn(v)
        } else {
            showOut(v)
        }
    }

    private fun showOut(v: View) {
        with(v) {
            visiable()
            alpha = 1f
            translationY = 0f
            animate()
                .setDuration(200)
                .translationY(v.height.toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        v.gone()
                        super.onAnimationEnd(animation)
                    }
                })
                .alpha(0f)
                .start()
        }
    }

    private fun showIn(v: View) {
        with(v) {
            visiable()
            alpha = 0f
            translationY = v.height.toFloat()
            animate()
                .setDuration(200)
                .translationY(0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        v.visiable()
                        super.onAnimationEnd(animation)
                    }
                })
                .alpha(1f)
                .start()
        }
    }
}

enum class RotateState { ROTATED, NONE }
package com.minux.mask_alarmi.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

object AnimUtil {
    fun startSlideOutAnim(view: View, translation: Float, isHorizontal: Boolean = true,) {
        val animator = ObjectAnimator.ofFloat(view, if (isHorizontal) "translationX" else "translationY", translation)
        animator.interpolator = AccelerateInterpolator()

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
            }
        })

        animator.duration = 500
        animator.start()
    }

    fun startSlideInAnim(view: View, translation: Float, isHorizontal: Boolean = true,) {
        val animator = ObjectAnimator.ofFloat(view, if (isHorizontal) "translationX" else "translationY", translation)
        animator.interpolator = DecelerateInterpolator()

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                view.visibility = View.VISIBLE
            }
        })

        animator.duration = 500
        animator.start()
    }

    fun expandAnim(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", -view.width.toFloat(), 0f)
        animator.duration = 35000

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                view.visibility = View.VISIBLE
            }
        })

        animator.start()
    }
}
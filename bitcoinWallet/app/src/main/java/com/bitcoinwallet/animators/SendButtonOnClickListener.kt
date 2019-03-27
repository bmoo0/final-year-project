package com.bitcoinwallet.animators

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Interpolator
import android.widget.ImageView
import com.bitcoinwallet.R
import com.bitcoinwallet.activities.HomeActivity

/**
 * Created by Ben Moore on 26/03/2019.
 */

class SendButtonOnClickListener @JvmOverloads internal constructor(
    private val context: HomeActivity,
    private val hidingSheet: View,
    private val interpolator: Interpolator? = null,
    private val openIcon: Drawable? = null,
    private val closeIcon: Drawable? = null) : View.OnClickListener {

    private val animatorSet = AnimatorSet()
    private val height: Int
    private var sendScreenShown = false

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
    }

    override fun onClick(view: View) {
        sendScreenShown = !sendScreenShown
        context.isSendScreenShown = sendScreenShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        // once I have icons maybe?
        updateIcon(view)

        val animator = ObjectAnimator.ofFloat(hidingSheet, "translationY", (if (sendScreenShown) 0 else height).toFloat())
        animator.duration = 500

        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)
        animator.start()
    }

    private fun updateIcon(view: View) {
        if (openIcon != null && closeIcon != null) {
            if (view !is ImageView) {
                throw IllegalArgumentException("updateIcon() must be called on an ImageView")
            }
            if (sendScreenShown) {
                view.setImageDrawable(closeIcon)
                view.setBackgroundColor(context.resources.getColor(R.color.colorSecondary))
            } else {
                view.setImageDrawable(openIcon)
                view.setBackgroundColor(context.resources.getColor(R.color.buttonGreen))
            }
        }
    }
}
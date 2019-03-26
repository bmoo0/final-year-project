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

class SettingsButtonOnClickListener @JvmOverloads internal constructor(
    private val context: HomeActivity,
    private val homeSheet: View,
    private val sendSheet: View,
    private val interpolator: Interpolator? = null,
    private val openIcon: Drawable? = null,
    private val closeIcon: Drawable? = null) : View.OnClickListener {

    private val animatorSet = AnimatorSet()
    private val height: Int
    private var backdropShown = false

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
    }

    override fun onClick(view: View) {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        // once I have icons maybe?
        updateIcon(view)

        // needs updating
        val translateY = height - context.resources.getDimensionPixelSize(R.dimen.show_settings_screen_reveal_height)

        val homeAnimator = ObjectAnimator.ofFloat(homeSheet, "translationY", (if (backdropShown) translateY else 0).toFloat())
        homeAnimator.duration = 500

        if (interpolator != null) {
            homeAnimator.interpolator = interpolator
        }

        if(context.isSendScreenShown) {
            context.isSendScreenShown = !context.isSendScreenShown

            val sendScreenAnimator = ObjectAnimator.ofFloat(sendSheet, "translationY", height.toFloat())
            if (interpolator != null) {
                sendScreenAnimator.interpolator = interpolator
            }
            sendScreenAnimator.duration = 500
            animatorSet.play(sendScreenAnimator)
            sendScreenAnimator.start()
        }

        animatorSet.play(homeAnimator)
        homeAnimator.start()
    }

    private fun updateIcon(view: View) {
        if (openIcon != null && closeIcon != null) {
            if (view !is ImageView) {
                throw IllegalArgumentException("updateIcon() must be called on an ImageView")
            }
            if (backdropShown) {
                view.setImageDrawable(closeIcon)
            } else {
                view.setImageDrawable(openIcon)
            }
        }
    }
}

package com.bitcoinwallet.animators

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Interpolator
import android.widget.ImageView
import com.bitcoinwallet.R
import com.bitcoinwallet.activities.HomeActivity
import kotlinx.android.synthetic.main.activity_home.*

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
        //updateIcon(view)
        val colourGreen = ContextCompat.getColor(context, R.color.buttonGreen)
        val colourRed = ContextCompat.getColor(context, R.color.colorSecondary)

        val animator = ObjectAnimator.ofFloat(hidingSheet, "translationY", (if (sendScreenShown) 0 else height).toFloat())
        val btnAnimator = if (sendScreenShown) ValueAnimator.ofObject(ArgbEvaluator(), colourRed, colourGreen)
        else ValueAnimator.ofObject(ArgbEvaluator(), colourGreen, colourRed)


        /*
        btnAnimator.addUpdateListener {
            context.send_btn_floating.background.
        }
        */

        animator.duration = 500
        btnAnimator.duration = 500

        if (interpolator != null) {
            animator.interpolator = interpolator
            btnAnimator.interpolator = interpolator
        }
        animatorSet.play(animator).with(btnAnimator)
        animator.start()
    }
}
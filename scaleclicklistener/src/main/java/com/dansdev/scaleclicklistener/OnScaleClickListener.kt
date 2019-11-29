package com.dansdev.scaleclicklistener

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.livinglifetechway.k4kotlin.orZero
import java.lang.ref.WeakReference

@SuppressLint("ClickableViewAccessibility")
abstract class OnScaleClickListener(private val duration: Long = DURATION,
                                    private val scale: Float = SCALE,
                                    private val withDebounced: Boolean = true,
                                    private val debounceTime: Int = 1000) : View.OnTouchListener, View.OnClickListener {

    companion object {
        const val DURATION = 100L
        const val SCALE = 0.9f
    }

    private var touchTime: Long = 0L
    private var diffTime: Long = 0L
    private var lastTimeClicked: Long = 0

    private lateinit var scaleXDownAnimator: ObjectAnimator
    private lateinit var scaleXUpAnimator: ObjectAnimator
    private lateinit var scaleYDownAnimator: ObjectAnimator
    private lateinit var scaleYUpAnimator: ObjectAnimator
    private lateinit var endAnimationListener: AnimatorListenerAdapter

    private var view: WeakReference<View>? = null
    private var createdAnimators = false
    private var pressed = false
    private var released = true
    private var rect: Rect? = null

    private var downSet: AnimatorSet? = null
    private var upSet: AnimatorSet? = null

    private fun createAnimators() {
        this.view?.let { view ->
            scaleXDownAnimator = ObjectAnimator.ofFloat(view.get(), View.SCALE_X, scale)
            scaleXUpAnimator = ObjectAnimator.ofFloat(view.get(), View.SCALE_X, 1.0f)
            scaleYDownAnimator = ObjectAnimator.ofFloat(view.get(), View.SCALE_Y, scale)
            scaleYUpAnimator = ObjectAnimator.ofFloat(view.get(), View.SCALE_Y, 1.0f)

            endAnimationListener = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    if (withDebounced) {
                        if (SystemClock.elapsedRealtime() - lastTimeClicked < debounceTime) {
                            return
                        }
                        lastTimeClicked = SystemClock.elapsedRealtime()
                    }
                    onClick(this@OnScaleClickListener.view?.get())
                }
            }

            upSet = AnimatorSet().apply {
                duration = this@OnScaleClickListener.duration
                interpolator = FastOutSlowInInterpolator()
                playTogether(scaleXUpAnimator, scaleYUpAnimator)
            }

            downSet = AnimatorSet().apply {
                duration = this@OnScaleClickListener.duration
                interpolator = AccelerateInterpolator()
                playTogether(scaleXDownAnimator, scaleYDownAnimator)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (view == null) view = WeakReference<View>(v)
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                setVisibleRect()
                touchTime = System.currentTimeMillis()
                if (!pressed) {
                    setScaleEffects(true)
                    pressed = true
                    released = false
                }
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!released) {
                    setVisibleRect()
                    if (!focusInView(event)) {
                        setScaleEffects(false)
                        released = true
                        pressed = false
                    }
                }
                false
            }
            MotionEvent.ACTION_UP -> {
                if (!released) {
                    setVisibleRect()
                    if (focusInView(event)) {
                        upSet?.addListener(endAnimationListener)
                    }
                    setScaleEffects(false)
                    released = true
                    pressed = false
                }
                false
            }
            MotionEvent.ACTION_CANCEL -> {
                setScaleEffects(false)
                released = true
                pressed = false
                false
            }
            else -> false
        }
    }

    private fun focusInView(event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        return x >= rect?.left.orZero() && x <= rect?.right.orZero() && y <= rect?.bottom.orZero() && y >= rect?.top.orZero()
    }

    private fun setVisibleRect() {
        rect = Rect()
        view?.get()?.getWindowVisibleDisplayFrame(rect)
    }

    private fun setScaleEffects(press: Boolean) {
        if (!createdAnimators) {
            createAnimators()
            createdAnimators = true
        }

        if (press) {
            upSet?.removeAllListeners()
            downSet?.cancel()
            downSet?.start()
        } else {
            diffTime = System.currentTimeMillis() - touchTime
            if (diffTime < duration) {
                upSet?.startDelay = duration - diffTime
            }
            upSet?.cancel()
            upSet?.start()
        }
    }
}
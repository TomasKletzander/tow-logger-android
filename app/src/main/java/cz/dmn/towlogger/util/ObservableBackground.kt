package cz.dmn.towlogger.util

import android.animation.ArgbEvaluator
import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable

class ObservableBackground : BaseObservable() {

    private var _colorResId = 0
    var colorResId: Int
    get() = _colorResId
    set(@ColorRes value) {
        _colorResId = value
        notifyChange()
    }
}

@BindingAdapter("android:background")
fun setBackground(view: View, background: ObservableBackground) {
    val colorTo = ContextCompat.getColor(view.context, background.colorResId)
    val colorFrom = (view.background as? ColorDrawable)?.color ?: colorTo
    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
    colorAnimation.duration = 400 // milliseconds
    colorAnimation.addUpdateListener { animator ->
        view.setBackgroundColor(animator.animatedValue as Int)
    }
    colorAnimation.start()
}

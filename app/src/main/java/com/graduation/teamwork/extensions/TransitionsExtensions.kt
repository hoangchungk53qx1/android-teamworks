import android.view.animation.Interpolator
import androidx.transition.TransitionSet

fun TransitionSet.setCommonInterpolator(interpolator: Interpolator): TransitionSet {
    (0 until transitionCount)
        .map { index -> getTransitionAt(index) }
        .forEach { transition -> transition?.interpolator = interpolator }

    return this
}
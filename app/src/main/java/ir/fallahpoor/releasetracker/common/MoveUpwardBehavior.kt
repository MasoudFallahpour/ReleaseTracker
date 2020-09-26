package ir.fallahpoor.releasetracker.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import kotlin.math.min

@Keep
class MoveUpwardBehavior
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is SnackbarLayout
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val translationY = min(0f, dependency.translationY - dependency.height)
        ViewCompat.animate(child)
            .translationY(translationY)
            .start()
        return true
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        ViewCompat.animate(child)
            .translationY(0f)
            .start()
    }

}
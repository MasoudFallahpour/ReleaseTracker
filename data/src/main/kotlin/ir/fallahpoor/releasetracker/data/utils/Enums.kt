package ir.fallahpoor.releasetracker.data.utils

import androidx.annotation.StringRes
import ir.fallahpoor.releasetracker.data.R

enum class NightMode(@StringRes val label: Int) {
    ON(R.string.on),
    OFF(R.string.off),
    AUTO(R.string.auto)
}

enum class SortOrder(@StringRes val label: Int) {
    A_TO_Z(R.string.a_to_z),
    Z_TO_A(R.string.z_to_a),
    PINNED_FIRST(R.string.pinned_first)
}
package ir.fallahpoor.releasetracker.data.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class LocalStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private companion object {
        const val KEY_SORTING_ORDER = "sorting_order"
    }

    fun setSortingOrder(sortingOrder: String) {
        putString(KEY_SORTING_ORDER, sortingOrder)
    }

    fun getSortingOrder(): String? {
        return getString(KEY_SORTING_ORDER)
    }

    private fun putString(key: String, value: String) {
        sharedPreferences.edit(commit = true) {
            putString(key, value)
        }
    }

    private fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

}
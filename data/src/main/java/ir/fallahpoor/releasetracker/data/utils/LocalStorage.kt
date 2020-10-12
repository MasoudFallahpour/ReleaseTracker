package ir.fallahpoor.releasetracker.data.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val rxkPrefs: RxkPrefs
) {

    private companion object {
        const val KEY_ORDER = "order"
        const val KEY_LAST_UPDATE_CHECK = "last_update_check"
        const val KEY_NIGHT_MODE = "night_mode"
    }

    fun setOrder(order: String) {
        putString(KEY_ORDER, order)
    }

    fun getOrder(): String? {
        return getString(KEY_ORDER)
    }

    fun setLastUpdateCheck(date: String) {
        rxkPrefs.string(KEY_LAST_UPDATE_CHECK)
            .set(date)
    }

    fun getLastUpdateCheck(): Flow<String> =
        rxkPrefs.string(KEY_LAST_UPDATE_CHECK, defaultValue = "N/A")
            .asFlow()

    fun getNightMode(): String? {
        return getString(KEY_NIGHT_MODE)
    }

    fun setNightMode(mode: String) {
        return putString(KEY_NIGHT_MODE, mode)
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
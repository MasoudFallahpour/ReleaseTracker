package ir.fallahpoor.releasetracker.data.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import com.afollestad.rxkprefs.coroutines.asFlow
import com.afollestad.rxkprefs.rxkPrefs
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private companion object {
        const val KEY_ORDER = "order"
        const val KEY_LAST_UPDATE_CHECK = "last_update_check"
    }

    fun setOrder(order: String) {
        putString(KEY_ORDER, order)
    }

    fun getOrder(): String? {
        return getString(KEY_ORDER)
    }

    fun setLastUpdateCheck(date: String) {
        putString(KEY_LAST_UPDATE_CHECK, date)
    }

    fun getLastUpdateCheck(): Flow<String> =
        rxkPrefs(sharedPreferences)
            .string(KEY_LAST_UPDATE_CHECK, defaultValue = "N/A")
            .asFlow()

    private fun putString(key: String, value: String) {
        sharedPreferences.edit(commit = true) {
            putString(key, value)
        }
    }

    private fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

}
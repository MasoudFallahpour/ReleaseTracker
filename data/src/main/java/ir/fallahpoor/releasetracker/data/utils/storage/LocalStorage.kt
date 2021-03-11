package ir.fallahpoor.releasetracker.data.utils.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.coroutines.asFlow
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val rxkPrefs: RxkPrefs
) : Storage {

    private companion object {
        const val KEY_ORDER = "order"
        const val KEY_LAST_UPDATE_CHECK = "last_update_check"
        const val KEY_NIGHT_MODE = "night_mode"
    }

    private val defaultNightMode = NightMode.AUTO

    override fun setSortOrder(sortOrder: SortOrder) {
        putString(KEY_ORDER, sortOrder.name)
    }

    override fun getSortOrder(): SortOrder {
        return SortOrder.valueOf(getString(KEY_ORDER) ?: SortOrder.A_TO_Z.name)
    }

    override fun setLastUpdateCheck(date: String) {
        rxkPrefs.string(KEY_LAST_UPDATE_CHECK)
            .set(date)
    }

    override fun getLastUpdateCheck(): Flow<String> =
        rxkPrefs.string(KEY_LAST_UPDATE_CHECK, defaultValue = "N/A")
            .asFlow()

    override fun getNightModeAsFlow(): Flow<NightMode> =
        rxkPrefs.string(KEY_NIGHT_MODE)
            .asFlow()
            .map { mode: String ->
                NightMode.valueOf(if (mode.isNotBlank()) mode else defaultNightMode.name)
            }

    override fun getNightMode(): NightMode {
        val nightModeStr = getString(KEY_NIGHT_MODE)
        return NightMode.valueOf(nightModeStr ?: defaultNightMode.name)
    }

    override fun setNightMode(nightMode: NightMode) {
        rxkPrefs.string(KEY_NIGHT_MODE)
            .set(nightMode.name)
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
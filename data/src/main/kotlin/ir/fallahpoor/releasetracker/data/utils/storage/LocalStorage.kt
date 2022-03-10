package ir.fallahpoor.releasetracker.data.utils.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

// TODO: Remove the runBlocking calls.

class LocalStorage @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Storage {

    companion object {
        const val KEY_SORT_ORDER = "sort_order"
        const val KEY_LAST_UPDATE_CHECK = "last_update_check"
        const val KEY_NIGHT_MODE = "night_mode"
    }

    private val defaultNightMode = NightMode.AUTO
    private val defaultSortOrder = SortOrder.A_TO_Z

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        putString(KEY_SORT_ORDER, sortOrder.name)
    }

    override fun getSortOrder(): SortOrder {
        var sortOrderStr: String?
        runBlocking {
            sortOrderStr = getString(KEY_SORT_ORDER)
        }
        return SortOrder.valueOf(sortOrderStr ?: SortOrder.A_TO_Z.name)
    }

    override fun getSortOrderAsFlow(): Flow<SortOrder> {
        val prefKey = stringPreferencesKey(KEY_SORT_ORDER)
        return dataStore.data.map { preferences ->
            SortOrder.valueOf(preferences[prefKey] ?: defaultSortOrder.name)
        }
    }

    override suspend fun setLastUpdateCheck(date: String) {
        putString(KEY_LAST_UPDATE_CHECK, date)
    }

    override fun getLastUpdateCheck(): Flow<String> {
        val prefKey = stringPreferencesKey(KEY_LAST_UPDATE_CHECK)
        return dataStore.data.map { preferences ->
            preferences[prefKey] ?: "N/A"
        }
    }

    override fun getNightModeAsFlow(): Flow<NightMode> {
        val prefKey = stringPreferencesKey(KEY_NIGHT_MODE)
        return dataStore.data.map { preferences ->
            NightMode.valueOf(preferences[prefKey] ?: defaultNightMode.name)
        }
    }

    override fun getNightMode(): NightMode {
        var nightModeStr: String?
        runBlocking {
            nightModeStr = getString(KEY_NIGHT_MODE)
        }
        return NightMode.valueOf(nightModeStr ?: defaultNightMode.name)
    }

    override suspend fun setNightMode(nightMode: NightMode) {
        putString(KEY_NIGHT_MODE, nightMode.name)
    }

    private suspend fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[prefKey] = value
        }
    }

    private suspend fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        val flow: Flow<String?> = dataStore.data.map { preferences ->
            preferences[prefKey]
        }
        return flow.first()
    }

}
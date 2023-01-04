package ir.fallahpoor.releasetracker.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import ir.fallahpoor.releasetracker.data.Constants
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
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
        const val DEFAULT_LAST_UPDATE_CHECK = Constants.NOT_AVAILABLE
        val DEFAULT_NIGHT_MODE = NightMode.AUTO
        val DEFAULT_SORT_ORDER = SortOrder.A_TO_Z
    }

    override suspend fun saveSortOrder(sortOrder: SortOrder) {
        putString(KEY_SORT_ORDER, sortOrder.name)
    }

    override fun getSortOrder(): SortOrder {
        var sortOrderStr: String?
        runBlocking {
            sortOrderStr = getString(KEY_SORT_ORDER)
        }
        return SortOrder.valueOf(sortOrderStr ?: DEFAULT_SORT_ORDER.name)
    }

    override fun getSortOrderAsFlow(): Flow<SortOrder> {
        val prefKey = stringPreferencesKey(KEY_SORT_ORDER)
        return dataStore.data.map { preferences ->
            SortOrder.valueOf(preferences[prefKey] ?: DEFAULT_SORT_ORDER.name)
        }
    }

    override suspend fun saveNightMode(nightMode: NightMode) {
        putString(KEY_NIGHT_MODE, nightMode.name)
    }

    override fun getNightMode(): NightMode {
        var nightModeStr: String?
        runBlocking {
            nightModeStr = getString(KEY_NIGHT_MODE)
        }
        return NightMode.valueOf(nightModeStr ?: DEFAULT_NIGHT_MODE.name)
    }

    override fun getNightModeAsFlow(): Flow<NightMode> {
        val prefKey = stringPreferencesKey(KEY_NIGHT_MODE)
        return dataStore.data.map { preferences ->
            NightMode.valueOf(preferences[prefKey] ?: DEFAULT_NIGHT_MODE.name)
        }
    }

    override suspend fun saveLastUpdateCheck(date: String) {
        putString(KEY_LAST_UPDATE_CHECK, date)
    }

    override fun getLastUpdateCheckAsFlow(): Flow<String> {
        val prefKey = stringPreferencesKey(KEY_LAST_UPDATE_CHECK)
        return dataStore.data.map { preferences ->
            preferences[prefKey] ?: Constants.NOT_AVAILABLE
        }
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
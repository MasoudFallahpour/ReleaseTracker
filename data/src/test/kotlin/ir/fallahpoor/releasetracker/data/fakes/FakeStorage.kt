package ir.fallahpoor.releasetracker.data.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.storage.LocalStorage
import ir.fallahpoor.releasetracker.data.storage.Storage
import kotlinx.coroutines.flow.Flow

class FakeStorage : Storage {

    private var sortOrderLiveData = MutableLiveData(LocalStorage.DEFAULT_SORT_ORDER)
    private val lastUpdateCheckDateLiveData =
        MutableLiveData(LocalStorage.DEFAULT_LAST_UPDATE_CHECK)
    private val nightModeLiveData = MutableLiveData(LocalStorage.DEFAULT_NIGHT_MODE)

    override suspend fun saveSortOrder(sortOrder: SortOrder) {
        sortOrderLiveData.value = sortOrder
    }

    override fun getSortOrder() = sortOrderLiveData.value!!

    override fun getSortOrderAsFlow(): Flow<SortOrder> = sortOrderLiveData.asFlow()

    override suspend fun saveNightMode(nightMode: NightMode) {
        nightModeLiveData.value = nightMode
    }

    override fun getNightMode() = nightModeLiveData.value!!

    override fun getNightModeAsFlow(): Flow<NightMode> = nightModeLiveData.asFlow()

    override suspend fun saveLastUpdateCheck(date: String) {
        lastUpdateCheckDateLiveData.value = date
    }

    override fun getLastUpdateCheckAsFlow(): Flow<String> = lastUpdateCheckDateLiveData.asFlow()

}
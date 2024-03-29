package ir.fallahpoor.releasetracker.data.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.storage.Storage
import kotlinx.coroutines.flow.Flow

class FakeStorage : Storage {

    private var sortOrderLiveData = MutableLiveData(SortOrder.A_TO_Z)
    private val lastUpdateCheckDateLiveData = MutableLiveData("N/A")
    private val nightModeLiveData = MutableLiveData(NightMode.AUTO)

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        sortOrderLiveData.value = sortOrder
    }

    override fun getSortOrder() = sortOrderLiveData.value!!

    override fun getSortOrderAsFlow(): Flow<SortOrder> = sortOrderLiveData.asFlow()

    override suspend fun setLastUpdateCheck(date: String) {
        lastUpdateCheckDateLiveData.value = date
    }

    override fun getLastUpdateCheck(): Flow<String> = lastUpdateCheckDateLiveData.asFlow()

    override fun getNightModeAsFlow(): Flow<NightMode> = nightModeLiveData.asFlow()

    override fun getNightMode() = nightModeLiveData.value!!

    override suspend fun setNightMode(nightMode: NightMode) {
        nightModeLiveData.value = nightMode
    }

}
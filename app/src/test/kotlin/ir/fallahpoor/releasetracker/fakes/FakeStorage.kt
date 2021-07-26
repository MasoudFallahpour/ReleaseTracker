package ir.fallahpoor.releasetracker.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import kotlinx.coroutines.flow.Flow

class FakeStorage : Storage {

    private var sortOrder = SortOrder.A_TO_Z
    private var lastUpdateCheckDateLiveData = MutableLiveData("N/A")
    private var nightModeLiveData = MutableLiveData(NightMode.AUTO)

    override fun setSortOrder(sortOrder: SortOrder) {
        this.sortOrder = sortOrder
    }

    override fun getSortOrder() = sortOrder

    override fun setLastUpdateCheck(date: String) {
        lastUpdateCheckDateLiveData.value = date
    }

    override fun getLastUpdateCheck(): Flow<String> = lastUpdateCheckDateLiveData.asFlow()

    override fun getNightModeAsFlow(): Flow<NightMode> = nightModeLiveData.asFlow()

    override fun getNightMode() = nightModeLiveData.value!!

    override fun setNightMode(nightMode: NightMode) {
        this.nightModeLiveData.value = nightMode
    }

}
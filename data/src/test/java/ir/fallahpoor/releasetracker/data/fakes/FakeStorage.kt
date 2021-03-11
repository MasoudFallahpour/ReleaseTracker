package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStorage : Storage {

    private var sortOrder = SortOrder.A_TO_Z
    private var lastUpdateCheckDate = "N/A"
    private var nightMode = NightMode.AUTO

    override fun setSortOrder(sortOrder: SortOrder) {
        this.sortOrder = sortOrder
    }

    override fun getSortOrder() = sortOrder

    override fun setLastUpdateCheck(date: String) {
        lastUpdateCheckDate = date
    }

    override fun getLastUpdateCheck(): Flow<String> =
        flow {
           emit(lastUpdateCheckDate)
        }

    override fun getNightModeAsFlow(): Flow<NightMode> =
        flow {
            emit(nightMode)
        }

    override fun getNightMode() = nightMode

    override fun setNightMode(nightMode: NightMode) {
        this.nightMode = nightMode
    }

}
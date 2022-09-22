package ir.fallahpoor.releasetracker.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.repository.storage.StorageRepository
import kotlinx.coroutines.flow.Flow

class FakeStorageRepository : StorageRepository {

    private var sortOrderLiveData = MutableLiveData(SortOrder.A_TO_Z)
    private val lastUpdateCheckDateLiveData = MutableLiveData("N/A")

    override fun getLastUpdateCheck(): Flow<String> = lastUpdateCheckDateLiveData.asFlow()

    override suspend fun saveLastUpdateCheck(date: String) {
        lastUpdateCheckDateLiveData.value = date
    }

    override suspend fun saveSortOrder(sortOrder: SortOrder) {
        sortOrderLiveData.value = sortOrder
    }

    override fun getSortOrder() = sortOrderLiveData.value!!

    override fun getSortOrderAsFlow(): Flow<SortOrder> = sortOrderLiveData.asFlow()

}
package ir.fallahpoor.releasetracker.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.repository.storage.StorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStorageRepository : StorageRepository {

    private var sortOrderLiveData = MutableLiveData(SortOrder.A_TO_Z)

    override fun getLastUpdateCheck(): Flow<String> = flow {
        emit(FakeLibraryRepository.LAST_UPDATE_CHECK)
    }

    override suspend fun setLastUpdateCheck(date: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        sortOrderLiveData.value = sortOrder
    }

    override fun getSortOrder() = sortOrderLiveData.value!!

    override fun getSortOrderAsFlow(): Flow<SortOrder> = sortOrderLiveData.asFlow()

}
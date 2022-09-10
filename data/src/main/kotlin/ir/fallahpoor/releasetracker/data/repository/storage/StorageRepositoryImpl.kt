package ir.fallahpoor.releasetracker.data.repository.storage

import ir.fallahpoor.releasetracker.data.storage.Storage
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(private val storage: Storage) : StorageRepository {

    override fun getLastUpdateCheck(): Flow<String> = storage.getLastUpdateCheck()

    override suspend fun setLastUpdateCheck(date: String) {
        storage.setLastUpdateCheck(date)
    }

    override fun getSortOrder(): SortOrder = storage.getSortOrder()

    override fun getSortOrderAsFlow(): Flow<SortOrder> = storage.getSortOrderAsFlow()

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        storage.setSortOrder(sortOrder)
    }

}
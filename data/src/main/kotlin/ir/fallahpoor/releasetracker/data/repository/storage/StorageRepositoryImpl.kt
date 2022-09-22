package ir.fallahpoor.releasetracker.data.repository.storage

import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.storage.Storage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(private val storage: Storage) : StorageRepository {

    override fun getLastUpdateCheck(): Flow<String> = storage.getLastUpdateCheck()

    override suspend fun saveLastUpdateCheck(date: String) {
        storage.saveLastUpdateCheck(date)
    }

    override fun getSortOrder(): SortOrder = storage.getSortOrder()

    override fun getSortOrderAsFlow(): Flow<SortOrder> = storage.getSortOrderAsFlow()

    override suspend fun saveSortOrder(sortOrder: SortOrder) {
        storage.saveSortOrder(sortOrder)
    }

}
package ir.fallahpoor.releasetracker.data.repository.storage

import ir.fallahpoor.releasetracker.data.SortOrder
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    fun getLastUpdateCheck(): Flow<String>

    suspend fun saveLastUpdateCheck(date: String)

    fun getSortOrder(): SortOrder

    fun getSortOrderAsFlow(): Flow<SortOrder>

    suspend fun saveSortOrder(sortOrder: SortOrder)

}
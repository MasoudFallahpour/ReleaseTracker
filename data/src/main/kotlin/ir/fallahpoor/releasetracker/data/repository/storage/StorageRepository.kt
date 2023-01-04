package ir.fallahpoor.releasetracker.data.repository.storage

import ir.fallahpoor.releasetracker.data.SortOrder
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    suspend fun saveSortOrder(sortOrder: SortOrder)

    fun getSortOrder(): SortOrder

    fun getSortOrderAsFlow(): Flow<SortOrder>

    suspend fun saveLastUpdateCheck(date: String)

    fun getLastUpdateCheckAsFlow(): Flow<String>

}
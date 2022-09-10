package ir.fallahpoor.releasetracker.data.repository.storage

import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow

interface StorageRepository {

    fun getLastUpdateCheck(): Flow<String>

    suspend fun setLastUpdateCheck(date: String)

    fun getSortOrder(): SortOrder

    fun getSortOrderAsFlow(): Flow<SortOrder>

    suspend fun setSortOrder(sortOrder: SortOrder)

}
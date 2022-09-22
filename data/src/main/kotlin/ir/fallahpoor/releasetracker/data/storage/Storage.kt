package ir.fallahpoor.releasetracker.data.storage

import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder
import kotlinx.coroutines.flow.Flow

interface Storage {

    suspend fun saveSortOrder(sortOrder: SortOrder)

    fun getSortOrder(): SortOrder

    fun getSortOrderAsFlow(): Flow<SortOrder>

    suspend fun saveLastUpdateCheck(date: String)

    fun getLastUpdateCheck(): Flow<String>

    fun getNightModeAsFlow(): Flow<NightMode>

    fun getNightMode(): NightMode

    suspend fun saveNightMode(nightMode: NightMode)

}
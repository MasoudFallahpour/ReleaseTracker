package ir.fallahpoor.releasetracker.data.utils.storage

import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow

interface Storage {

    fun setSortOrder(sortOrder: SortOrder)

    fun getSortOrder(): SortOrder

    fun setLastUpdateCheck(date: String)

    fun getLastUpdateCheck(): Flow<String>

    fun getNightModeAsFlow(): Flow<NightMode>

    fun getNightMode(): NightMode

    suspend fun setNightMode(nightMode: NightMode)

}
package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NightModeRepositoryImpl @Inject constructor(
    private val storage: Storage
) : NightModeRepository {

    override suspend fun setNightMode(nightMode: NightMode) {
        storage.setNightMode(nightMode)
    }

    override fun getNightMode(): NightMode = storage.getNightMode()

    override fun getNightModeAsFlow(): Flow<NightMode> = storage.getNightModeAsFlow()

}
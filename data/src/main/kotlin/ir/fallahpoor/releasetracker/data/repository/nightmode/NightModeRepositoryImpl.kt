package ir.fallahpoor.releasetracker.data.repository.nightmode

import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.storage.Storage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// TODO Add tests

class NightModeRepositoryImpl @Inject constructor(
    private val storage: Storage
) : NightModeRepository {

    override suspend fun setNightMode(nightMode: NightMode) {
        storage.saveNightMode(nightMode)
    }

    override fun getNightMode(): NightMode = storage.getNightMode()

    override fun getNightModeAsFlow(): Flow<NightMode> = storage.getNightModeAsFlow()

}
package ir.fallahpoor.releasetracker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.data.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class UpdateVersionsWorker
@WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val libraryRepository: LibraryRepository,
    private val networkUtils: NetworkUtils,
    private val localStorage: LocalStorage
) : CoroutineWorker(context, workerParams) {

    private val simpleDateFormat = SimpleDateFormat("MMM dd HH:mm", Locale.US)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val result: Result =
            if (!networkUtils.networkReachable()) {
                Result.retry()
            } else {
                libraryRepository.getLibraries()
                    .forEach { library: Library ->
                        launch {
                            getLatestVersion(library)
                        }
                    }
                Timber.d("Update check: %s", simpleDateFormat.format(Date()))
                localStorage.setLastUpdateCheck(simpleDateFormat.format(Date()))
                Result.success()
            }

        result

    }

    private suspend fun getLatestVersion(library: Library) {

        try {

            val libraryVersion: String =
                libraryRepository.getLibraryVersion(library.name, library.url)

            val library = library.copy(version = libraryVersion)
            libraryRepository.updateLibrary(library)

            Timber.d("Update SUCCESS (%s): %s", library.name, libraryVersion)

        } catch (t: Throwable) {
            Timber.d("Update FAILURE (%s): %s", library.name, t.message)
        }

    }

}
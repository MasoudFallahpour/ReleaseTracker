package ir.fallahpoor.releasetracker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UpdateVersionsWorker
@WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val libraryRepository: LibraryRepository,
    private val networkUtils: NetworkUtils
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val result: Result =
            if (!networkUtils.networkReachable()) {
                Result.retry()
            } else {
                libraryRepository.getLibraries()
                    .map { library: Library ->
                        launch {
                            getLatestVersion(library)
                        }
                    }
                Result.success()
            }

        result

    }

    private suspend fun getLatestVersion(library: Library) {

        try {

            val libraryVersion: String =
                libraryRepository.getLibraryVersion(library.libraryName, library.libraryUrl)

            val library = Library(library.libraryName, library.libraryUrl, libraryVersion)
            libraryRepository.updateLibrary(library)

            Timber.d("Update SUCCESS (%s): %s", library.libraryName, libraryVersion)

        } catch (t: Throwable) {
            Timber.d("Update FAILURE (%s): %s", library.libraryName, t.message)
        }

    }

}
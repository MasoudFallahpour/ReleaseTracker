package ir.fallahpoor.releasetracker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.GithubResponse
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.NetworkUtils
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateVersionsWorker
@WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val librariesDao: LibraryDao,
    private val githubWebservice: GithubWebservice,
    private val networkUtils: NetworkUtils
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val GITHUB_BASE_URL = "https://github.com/"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val result: Result =
            if (!networkUtils.networkReachable()) {
                Result.retry()
            } else {
                val libraries: List<Library> = librariesDao.getAll()
                libraries.map {
                    launch {
                        getLatestVersion(it)
                    }
                }
                Result.success()
            }

        result

    }

    private suspend fun getLatestVersion(library: Library) {

        val libraryPath = library.libraryUrl.removePrefix(GITHUB_BASE_URL)
        val libraryOwner = libraryPath.substring(0 until libraryPath.indexOf("/"))
        val libraryRepo = libraryPath.substring(libraryPath.indexOf("/") + 1)

        try {
            val githubResponse: GithubResponse = githubWebservice.getLatestVersion(
                libraryOwner,
                libraryRepo
            )
            librariesDao.update(
                Library(library.libraryName, library.libraryUrl, githubResponse.name)
            )
        } catch (t: Throwable) {
            Log.d("@@@@@@", "Update failed: " + t.message)
        }

    }

}
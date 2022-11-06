package ir.fallahpoor.releasetracker.data.repository.library

import android.database.sqlite.SQLiteConstraintException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.fakes.FakeData
import ir.fallahpoor.releasetracker.data.fakes.FakeGitHubApi
import ir.fallahpoor.releasetracker.data.fakes.FakeLibraryDao
import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.data.toLibrary
import ir.fallahpoor.releasetracker.data.toLibraryEntity
import ir.fallahpoor.releasetracker.data.toSearchRepositoriesResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var libraryRepository: LibraryRepositoryImpl
    private lateinit var fakeLibraryDao: FakeLibraryDao
    private lateinit var fakeGitHubApi: FakeGitHubApi

    @Before
    fun runBeforeEachTest() {
        fakeLibraryDao = FakeLibraryDao()
        fakeGitHubApi = FakeGitHubApi()
        libraryRepository = LibraryRepositoryImpl(
            libraryDao = fakeLibraryDao,
            gitHubApi = fakeGitHubApi
        )
    }

    @Test
    fun `getLibrary returns the library given that the library exists`() = runTest {

        // Given
        val expectedLibrary: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(expectedLibrary.toLibraryEntity())

        // When
        val actualLibrary: Library? = libraryRepository.getLibrary(expectedLibrary.name)

        // Then
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

    }

    @Test
    fun `getLibrary returns null given that the library does not exist`() = runTest {

        // When
        val actualLibrary: Library? = libraryRepository.getLibrary("SomeNonExistentLibraryName")

        // Then
        Truth.assertThat(actualLibrary).isNull()

    }

    @Test
    fun `getLibraryVersion returns the version of the library`() = runTest {

        // When
        val version: String = libraryRepository.getLibraryVersion(
            FakeData.ReleaseTracker.NAME,
            FakeData.ReleaseTracker.URL
        )

        // Then
        Truth.assertThat(version).isEqualTo(FakeData.ReleaseTracker.VERSION)

    }

    @Test
    fun `addLibrary adds the library to the database given that it's not already added`() =
        runTest {

            // Given
            val expectedLibrary: Library = FakeData.Coil.library

            // When
            libraryRepository.addLibrary(
                libraryName = expectedLibrary.name,
                libraryUrl = expectedLibrary.url,
                libraryVersion = expectedLibrary.version
            )

            // Then
            val actualLibrary: Library? = fakeLibraryDao.get(expectedLibrary.name)?.toLibrary()
            Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

        }

    @Test(expected = SQLiteConstraintException::class)
    fun `addLibrary throws an exception given that the library is already added`() =
        runTest {

            // Given
            val library: Library = FakeData.Coil.library
            fakeLibraryDao.insert(library.toLibraryEntity())

            // When
            libraryRepository.addLibrary(
                libraryName = library.name,
                libraryUrl = library.url,
                libraryVersion = library.version
            )

        }

    @Test
    fun `deleteLibrary deletes the library from the database given that it exists`() = runTest {

        // Given
        fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())

        // When
        libraryRepository.deleteLibrary(FakeData.ReleaseTracker.library)

        // Then
        val actualLibraries = fakeLibraryDao.getAll().map { it.toLibrary() }
        Truth.assertThat(actualLibraries).isEmpty()

    }

    @Test
    fun `deleteLibrary does nothing given that the library does not exist`() = runTest {

        // Given
        fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())

        // When
        libraryRepository.deleteLibrary(FakeData.Coil.library)

        // Then
        val actualLibraries = fakeLibraryDao.getAll().map { it.toLibrary() }
        Truth.assertThat(actualLibraries).isEqualTo(listOf(FakeData.ReleaseTracker.library))

    }

    @Test
    fun `updateLibrary updates the library given that it exists`() = runTest {

        // Given
        val library: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(library.toLibraryEntity())

        // When
        val expectedLibrary = library.copy(version = "0.3")
        libraryRepository.updateLibrary(expectedLibrary)

        // Then
        val actualLibrary = fakeLibraryDao.get(library.name)?.toLibrary()
        Truth.assertThat(actualLibrary).isEqualTo(expectedLibrary)

    }

    @Test
    fun `pinLibrary pins the given library`() = runTest {

        // Given
        val library: Library = FakeData.ReleaseTracker.library
        fakeLibraryDao.insert(library.toLibraryEntity())

        // When
        libraryRepository.pinLibrary(library, true)

        // Then
        val actualLibrary = fakeLibraryDao.get(library.name)?.toLibrary()
        Truth.assertThat(actualLibrary?.isPinned).isTrue()

    }

    @Test
    fun `pinLibrary unpins the given library`() = runTest {

        // Given
        val library: Library = FakeData.Coroutines.library
        fakeLibraryDao.insert(library.toLibraryEntity())

        // When
        libraryRepository.pinLibrary(library, false)

        // Then
        val actualLibrary = fakeLibraryDao.get(library.name)?.toLibrary()
        Truth.assertThat(actualLibrary?.isPinned).isFalse()

    }

    @Test
    fun `getLibraries returns all the libraries sorted by name`() = runTest {

        // Given
        fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())
        fakeLibraryDao.insert(FakeData.Coil.library.toLibraryEntity())

        // When
        val actualLibraries = libraryRepository.getLibraries()

        // Then
        val expectedLibraries = listOf(FakeData.Coil.library, FakeData.ReleaseTracker.library)
        Truth.assertThat(actualLibraries).isEqualTo(expectedLibraries)

    }

    @Test
    fun `getLibrariesAsFlow returns all the libraries sorted by name`() =
        runTest {

            // Given
            fakeLibraryDao.insert(FakeData.ReleaseTracker.library.toLibraryEntity())
            fakeLibraryDao.insert(FakeData.Coil.library.toLibraryEntity())
            fakeLibraryDao.insert(FakeData.Timber.library.toLibraryEntity())

            // When
            val actualLibraries: List<Library> = libraryRepository.getLibrariesAsFlow().first()

            // Then
            val expectedLibraries = listOf(
                FakeData.Coil.library,
                FakeData.ReleaseTracker.library,
                FakeData.Timber.library
            )
            Truth.assertThat(actualLibraries).isEqualTo(expectedLibraries)

        }

    @Test
    fun `searchLibraries returns a non empty list given that there are matched libraries`() =
        runTest {

            // Given
            val searchQuery = "co"
            val expectedSearchResults = fakeGitHubApi.searchRepositories(searchQuery).items
                .map { it.toSearchRepositoriesResult() }

            // When
            val actualSearchResults = libraryRepository.searchLibraries(searchQuery)

            // Then
            Truth.assertThat(actualSearchResults).isEqualTo(expectedSearchResults)

        }

    @Test
    fun `searchLibraries returns an empty list given that no library is found`() = runTest {

        // Given
        val searchQuery = "SomeNonExistentLibrary"

        // When
        val searchResults = libraryRepository.searchLibraries(searchQuery)

        // Then
        Truth.assertThat(searchResults).isEmpty()

    }

}
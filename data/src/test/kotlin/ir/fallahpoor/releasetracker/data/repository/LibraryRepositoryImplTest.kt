package ir.fallahpoor.releasetracker.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.TestData.COIL
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_NAME_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_NAME_2
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_URL_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_URL_2
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_VERSION_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_VERSION_2
import ir.fallahpoor.releasetracker.data.TestData.RELEASE_TRACKER
import ir.fallahpoor.releasetracker.data.TestData.TIMBER
import ir.fallahpoor.releasetracker.data.TestData.VERSION_1
import ir.fallahpoor.releasetracker.data.fakes.FakeGithubApi
import ir.fallahpoor.releasetracker.data.fakes.FakeLibraryDao
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.data.repository.library.LibraryMapper
import ir.fallahpoor.releasetracker.data.repository.library.LibraryRepositoryImpl
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var libraryRepository: LibraryRepositoryImpl
    private lateinit var fakeLibraryDao: FakeLibraryDao
    private lateinit var fakeStorage: FakeStorage
    private lateinit var libraryMapper: LibraryMapper

    @Before
    fun runBeforeEachTest() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeStorage = FakeStorage()
        fakeLibraryDao = FakeLibraryDao()
        libraryMapper = LibraryMapper()
        libraryRepository = LibraryRepositoryImpl(
            storage = fakeStorage,
            libraryDao = fakeLibraryDao,
            githubWebservice = FakeGithubApi(),
            libraryMapper = libraryMapper
        )
    }

    @Before
    fun runAfterEachTest() {
        Dispatchers.resetMain()
    }

    @Test
    fun `get library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)

        // When
        val library: Library? = libraryRepository.getLibrary(LIBRARY_NAME_1)

        // Then
        Truth.assertThat(library).isEqualTo(RELEASE_TRACKER)

    }

    @Test
    fun `library version name is returned when it is available`() = runTest {

        // Given

        // When
        val version: String = libraryRepository.getLibraryVersion(LIBRARY_NAME_1, LIBRARY_URL_1)

        // Then
        Truth.assertThat(version).isEqualTo(VERSION_1)

    }

    @Test
    fun `library tag name is returned when its version name is not available`() = runTest {

        // Given

        // When
        val version: String = libraryRepository.getLibraryVersion(LIBRARY_NAME_2, LIBRARY_URL_2)

        // Then
        Truth.assertThat(version).isEqualTo(LIBRARY_VERSION_2)

    }

    @Test
    fun `add library`() = runTest {

        // Given

        // When
        libraryRepository.addLibrary(LIBRARY_NAME_1, LIBRARY_URL_1, LIBRARY_VERSION_1)

        // Then
        val library: Library? = libraryRepository.getLibrary(LIBRARY_NAME_1)
        Truth.assertThat(library).isEqualTo(RELEASE_TRACKER)

    }

    @Test
    fun `delete library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)
        fakeLibraryDao.insert(COIL)

        // When
        libraryRepository.deleteLibrary(libraryMapper.map(RELEASE_TRACKER))

        // Then
        Truth.assertThat(libraryRepository.getLibraries()).isEqualTo(listOf(COIL))

    }

    @Test
    fun `update library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)

        // When
        val updatedLibrary = RELEASE_TRACKER.copy(version = "0.3")
        libraryRepository.updateLibrary(libraryMapper.map(updatedLibrary))

        // Then
        Truth.assertThat(libraryRepository.getLibrary(RELEASE_TRACKER.name))
            .isEqualTo(updatedLibrary)

    }

    @Test
    fun `pin library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)

        // When
        libraryRepository.pinLibrary(libraryMapper.map(RELEASE_TRACKER), true)

        // Then
        val pinnedLibrary = RELEASE_TRACKER.copy(pinned = 1)
        Truth.assertThat(libraryRepository.getLibrary(RELEASE_TRACKER.name))
            .isEqualTo(pinnedLibrary)

    }

    @Test
    fun `unpin library`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER.copy(pinned = 1))

        // When
        libraryRepository.pinLibrary(libraryMapper.map(RELEASE_TRACKER), false)

        // Then
        val unpinnedLibrary = RELEASE_TRACKER.copy(pinned = 0)
        Truth.assertThat(libraryRepository.getLibrary(RELEASE_TRACKER.name))
            .isEqualTo(unpinnedLibrary)

    }

    @Test
    fun `get libraries`() = runTest {

        // Given
        fakeLibraryDao.insert(RELEASE_TRACKER)
        fakeLibraryDao.insert(COIL)

        // When
        val libraries = libraryRepository.getLibraries()

        // Then
        Truth.assertThat(libraries).isEqualTo(listOf(COIL, RELEASE_TRACKER))

    }

    @Test
    fun `flow of libraries should return all libraries sorted by name in ascending order`() =
        runTest {

            // Given
            fakeLibraryDao.insert(RELEASE_TRACKER)
            fakeLibraryDao.insert(COIL)
            fakeLibraryDao.insert(TIMBER)

            // When
            val libraries: List<Library> = libraryRepository.getLibrariesAsFlow().first()

            // Then
            Truth.assertThat(libraries)
                .isEqualTo(listOf(COIL, RELEASE_TRACKER, TIMBER))

        }

    @Test
    fun `get last update check date`() = runTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March"
        fakeStorage.setLastUpdateCheck(expectedLastUpdateCheckDate)

        // When
        val actualLastUpdateCheck: String = libraryRepository.getLastUpdateCheck().first()

        // Then
        Truth.assertThat(actualLastUpdateCheck).isEqualTo(expectedLastUpdateCheckDate)

    }

    @Test
    fun `set last update check date`() = runTest {

        // Given
        val lastUpdateCheckDate = "15:30, March"

        // When
        libraryRepository.setLastUpdateCheck(lastUpdateCheckDate)

        // Then
        Truth.assertThat(fakeStorage.getLastUpdateCheck().first()).isEqualTo(lastUpdateCheckDate)

    }

    @Test
    fun `set sort order`() = runTest {

        // Given
        fakeStorage.setSortOrder(SortOrder.Z_TO_A)

        // When
        libraryRepository.setSortOrder(SortOrder.PINNED_FIRST)

        // Then
        Truth.assertThat(fakeStorage.getSortOrder()).isEqualTo(SortOrder.PINNED_FIRST)

    }

    @Test
    fun `get sort order`() = runTest {

        // Given
        fakeStorage.setSortOrder(SortOrder.Z_TO_A)

        // When
        val actualSortOrder = libraryRepository.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(SortOrder.Z_TO_A)

    }

    // TODO add tests for getSortOrderAsFlow()

}
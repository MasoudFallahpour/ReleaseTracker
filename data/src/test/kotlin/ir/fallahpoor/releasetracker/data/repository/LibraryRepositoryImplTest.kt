package ir.fallahpoor.releasetracker.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainCoroutineScopeRule
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_NAME_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_NAME_2
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_URL_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_URL_2
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_VERSION_1
import ir.fallahpoor.releasetracker.data.TestData.LIBRARY_VERSION_2
import ir.fallahpoor.releasetracker.data.TestData.TEST_LIBRARY_1
import ir.fallahpoor.releasetracker.data.TestData.TEST_LIBRARY_2
import ir.fallahpoor.releasetracker.data.TestData.TEST_LIBRARY_3
import ir.fallahpoor.releasetracker.data.TestData.VERSION_1
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.fakes.FakeGithubWebService
import ir.fallahpoor.releasetracker.data.fakes.FakeLibraryDao
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()
    private lateinit var libraryRepository: LibraryRepositoryImpl
    private val fakeLibraryDao = FakeLibraryDao()
    private val fakeStorage = FakeStorage()

    @Before
    fun runBeforeEachTest() {
        libraryRepository = LibraryRepositoryImpl(
            storage = fakeStorage,
            libraryDao = fakeLibraryDao,
            githubWebservice = FakeGithubWebService()
        )
    }

    @Test
    fun test_addLibrary() = runBlockingTest {

        // Given

        // When
        libraryRepository.addLibrary(LIBRARY_NAME_1, LIBRARY_URL_1, LIBRARY_VERSION_1)

        // Then
        val library: Library? = libraryRepository.getLibrary(LIBRARY_NAME_1)
        Truth.assertThat(library).isEqualTo(TEST_LIBRARY_1)

    }

    @Test
    fun test_getLibrary() = runBlockingTest {

        // Given
        fakeLibraryDao.insert(TEST_LIBRARY_1)

        // When
        val library: Library? = libraryRepository.getLibrary(LIBRARY_NAME_1)

        // Then
        Truth.assertThat(library).isEqualTo(TEST_LIBRARY_1)

    }

    @Test
    fun test_deleteLibrary() = runBlockingTest {

        // Given
        fakeLibraryDao.insert(TEST_LIBRARY_1)

        // When
        libraryRepository.deleteLibrary(TEST_LIBRARY_1)

        // Then
        Truth.assertThat(libraryRepository.getAllLibraries()).isEqualTo(emptyList<Library>())

    }

    @Test
    fun test_updateLibrary() = runBlockingTest {

        // Given
        fakeLibraryDao.insert(TEST_LIBRARY_1)

        // When
        val updatedLibrary = TEST_LIBRARY_1.copy(version = "0.3")
        libraryRepository.updateLibrary(updatedLibrary)

        // Then
        Truth.assertThat(libraryRepository.getLibrary(TEST_LIBRARY_1.name))
            .isEqualTo(updatedLibrary)

    }

    @Test
    fun `pinLibrary() should pin the library`() = runBlockingTest {

        // Given
        fakeLibraryDao.insert(TEST_LIBRARY_1)

        // When
        libraryRepository.pinLibrary(TEST_LIBRARY_1, true)

        // Then
        val pinnedLibrary = TEST_LIBRARY_1.copy(pinned = 1)
        Truth.assertThat(libraryRepository.getLibrary(TEST_LIBRARY_1.name)).isEqualTo(pinnedLibrary)

    }

    @Test
    fun `pinLibrary() should unpin the library`() = runBlockingTest {

        // Given
        fakeLibraryDao.insert(TEST_LIBRARY_1.copy(pinned = 1))

        // When
        libraryRepository.pinLibrary(TEST_LIBRARY_1, false)

        // Then
        val unpinnedLibrary = TEST_LIBRARY_1.copy(pinned = 0)
        Truth.assertThat(libraryRepository.getLibrary(TEST_LIBRARY_1.name))
            .isEqualTo(unpinnedLibrary)

    }

    @Test
    fun test_getLibraries() = runBlockingTest {

        // Given
        fakeLibraryDao.insert(TEST_LIBRARY_1)
        fakeLibraryDao.insert(TEST_LIBRARY_2)

        // When
        val libraries = libraryRepository.getAllLibraries()

        // Then
        Truth.assertThat(libraries).isEqualTo(listOf(TEST_LIBRARY_2, TEST_LIBRARY_1))

    }

    @Test
    fun `getLibraryVersion() should return version name`() = runBlockingTest {

        // Given

        // When
        val version: String = libraryRepository.getLibraryVersion(LIBRARY_NAME_1, LIBRARY_URL_1)

        // Then
        Truth.assertThat(version).isEqualTo(VERSION_1)

    }

    @Test
    fun `getLibraryVersion() should return tag name when version name is empty`() =
        runBlockingTest {

            // Given

            // When
            val version: String = libraryRepository.getLibraryVersion(LIBRARY_NAME_2, LIBRARY_URL_2)

            // Then
            Truth.assertThat(version).isEqualTo(LIBRARY_VERSION_2)

        }

    @Test
    fun `getLibraries() should return matched libraries sorted by name in ascending order`() =
        runBlockingTest {

            // Given
            fakeLibraryDao.insert(TEST_LIBRARY_1)
            fakeLibraryDao.insert(TEST_LIBRARY_2)
            fakeLibraryDao.insert(TEST_LIBRARY_3)

            // When
            val libraries: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.A_TO_Z,
                searchTerm = "er"
            ).first()

            // Then
            Truth.assertThat(libraries).isEqualTo(listOf(TEST_LIBRARY_1, TEST_LIBRARY_3))

        }

    @Test
    fun `getLibraries() should return matched libraries sorted by name in descending order`() =
        runBlockingTest {

            // Given
            fakeLibraryDao.insert(TEST_LIBRARY_1)
            fakeLibraryDao.insert(TEST_LIBRARY_2)
            fakeLibraryDao.insert(TEST_LIBRARY_3)

            // When
            val libraries: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.Z_TO_A,
                searchTerm = "er"
            ).first()

            // Then
            Truth.assertThat(libraries).isEqualTo(listOf(TEST_LIBRARY_3, TEST_LIBRARY_1))

        }

    @Test
    fun `getLibraries() should return matched libraries sorted by pinned libraries first`() =
        runBlockingTest {

            // Given
            val testLibrary1 = TEST_LIBRARY_1.copy(pinned = 1)
            val testLibrary2 = TEST_LIBRARY_2
            val testLibrary3 = TEST_LIBRARY_3.copy(pinned = 1)
            fakeLibraryDao.insert(testLibrary1)
            fakeLibraryDao.insert(testLibrary2)
            fakeLibraryDao.insert(testLibrary3)

            // When
            val libraries: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.PINNED_FIRST,
                searchTerm = "er"
            ).first()

            // Then
            Truth.assertThat(libraries).isEqualTo(listOf(testLibrary1, testLibrary3))

        }

    @Test
    fun `getLibraries() should return no library when there is no match`() =
        runBlockingTest {

            // Given
            fakeLibraryDao.insert(TEST_LIBRARY_1)
            fakeLibraryDao.insert(TEST_LIBRARY_2)
            fakeLibraryDao.insert(TEST_LIBRARY_3)

            // When
            val libraries: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.PINNED_FIRST,
                searchTerm = "ffefefe"
            ).first()

            // Then
            Truth.assertThat(libraries).isEqualTo(emptyList<Library>())

        }

    @Test
    fun `getLibraries() should return all libraries sorted by name in ascending order, when search term is empty`() =
        runBlockingTest {

            // Given
            fakeLibraryDao.insert(TEST_LIBRARY_1)
            fakeLibraryDao.insert(TEST_LIBRARY_2)
            fakeLibraryDao.insert(TEST_LIBRARY_3)

            // When
            val libraries: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.PINNED_FIRST,
                searchTerm = ""
            ).first()

            // Then
            Truth.assertThat(libraries)
                .isEqualTo(listOf(TEST_LIBRARY_2, TEST_LIBRARY_1, TEST_LIBRARY_3))

        }

    @Test
    fun `getLibraries() should be case insensitive regarding the search term`() =
        runBlockingTest {

            // Given
            fakeLibraryDao.insert(TEST_LIBRARY_1)
            fakeLibraryDao.insert(TEST_LIBRARY_2)
            fakeLibraryDao.insert(TEST_LIBRARY_3)

            // When
            val libraries1: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.PINNED_FIRST,
                searchTerm = "er"
            ).first()
            val libraries2: List<Library> = libraryRepository.getLibraries(
                sortOrder = SortOrder.PINNED_FIRST,
                searchTerm = "Er"
            ).first()

            // Then
            Truth.assertThat(libraries1).isEqualTo(libraries2)

        }

    @Test
    fun test_setLastUpdateCheck() = runBlockingTest {

        // Given
        val lastUpdateCheckDate = "15:30, March"

        // When
        libraryRepository.setLastUpdateCheck(lastUpdateCheckDate)

        // Then
        Truth.assertThat(fakeStorage.getLastUpdateCheck().first()).isEqualTo(lastUpdateCheckDate)

    }

    @Test
    fun test_getLastUpdateCheck() = runBlockingTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March"
        fakeStorage.setLastUpdateCheck(expectedLastUpdateCheckDate)

        // When
        val actualLastUpdateCheck: String = libraryRepository.getLastUpdateCheck().first()

        // Then
        Truth.assertThat(actualLastUpdateCheck).isEqualTo(expectedLastUpdateCheckDate)

    }

}
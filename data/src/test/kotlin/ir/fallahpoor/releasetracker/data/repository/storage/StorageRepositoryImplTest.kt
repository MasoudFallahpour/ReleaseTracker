package ir.fallahpoor.releasetracker.data.repository.storage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StorageRepositoryImplTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var storageRepository: StorageRepository
    private lateinit var fakeStorage: FakeStorage

    @Before
    fun runBeforeEachTest() {
        fakeStorage = FakeStorage()
        storageRepository = StorageRepositoryImpl(fakeStorage)
    }

    @Test
    fun `getLastUpdateCheck returns the date when it's available`() = runTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March"
        fakeStorage.saveLastUpdateCheck(expectedLastUpdateCheckDate)

        // When
        val actualLastUpdateCheck: String = storageRepository.getLastUpdateCheck().first()

        // Then
        Truth.assertThat(actualLastUpdateCheck).isEqualTo(expectedLastUpdateCheckDate)

    }

    @Test
    fun `getLastUpdateCheck returns NA when there is no date available`() = runTest {

        // When
        val actualLastUpdateCheck: String = storageRepository.getLastUpdateCheck().first()

        // Then
        Truth.assertThat(actualLastUpdateCheck).isEqualTo("N/A")

    }

    @Test
    fun `saveLastUpdateCheck saves the last update check date`() = runTest {

        // Given
        val lastUpdateCheckDate = "15:30, March"

        // When
        storageRepository.saveLastUpdateCheck(lastUpdateCheckDate)

        // Then
        Truth.assertThat(fakeStorage.getLastUpdateCheck().first()).isEqualTo(lastUpdateCheckDate)

    }

    @Test
    fun `saveSortOrder saves the sort order`() = runTest {

        // Given
        fakeStorage.saveSortOrder(SortOrder.Z_TO_A)

        // When
        storageRepository.saveSortOrder(SortOrder.PINNED_FIRST)

        // Then
        Truth.assertThat(fakeStorage.getSortOrder()).isEqualTo(SortOrder.PINNED_FIRST)

    }

    @Test
    fun `getSortOrder returns the saved sort order given that there is a saved sort order`() =
        runTest {

            // Given
            fakeStorage.saveSortOrder(SortOrder.Z_TO_A)

            // When
            val actualSortOrder = storageRepository.getSortOrder()

            // Then
            Truth.assertThat(actualSortOrder).isEqualTo(SortOrder.Z_TO_A)

        }

    @Test
    fun `getSortOrder returns the default saved sort order given that there is no saved sort order`() =
        runTest {

            // When
            val actualSortOrder = storageRepository.getSortOrder()

            // Then
            Truth.assertThat(actualSortOrder).isEqualTo(SortOrder.A_TO_Z)

        }

    @Test
    fun a() = runTest {

        // Given
        val actualSortOrders = mutableListOf<SortOrder>()
        val job = launch(UnconfinedTestDispatcher()) {
            storageRepository.getSortOrderAsFlow().toList(actualSortOrders)
        }

        // When
        with(storageRepository) {
            saveSortOrder(SortOrder.PINNED_FIRST)
            saveSortOrder(SortOrder.Z_TO_A)
            saveSortOrder(SortOrder.A_TO_Z)
        }

        // Then
        val expectedSortOrders =
            listOf(SortOrder.A_TO_Z, SortOrder.PINNED_FIRST, SortOrder.Z_TO_A, SortOrder.A_TO_Z)
        Truth.assertThat(actualSortOrders.size).isEqualTo(expectedSortOrders.size)
        actualSortOrders.zip(expectedSortOrders) { actualSortOrder, expectedSortOrder ->
            Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)
        }

        job.cancel()

    }

}
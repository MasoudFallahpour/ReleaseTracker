package ir.fallahpoor.releasetracker.data.repository.storage

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import ir.fallahpoor.releasetracker.data.MainDispatcherRule
import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.fakes.FakeStorage
import ir.fallahpoor.releasetracker.data.storage.LocalStorage
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
    fun `sort order is saved correctly`() = runTest {

        // Given
        val expectedSortOrder = SortOrder.Z_TO_A

        // When
        storageRepository.saveSortOrder(expectedSortOrder)

        // Then
        val actualSortOrder = fakeStorage.getSortOrder()
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun `saved sort order is returned correctly`() = runTest {

        // Given
        val expectedSortOrder = SortOrder.Z_TO_A
        fakeStorage.saveSortOrder(expectedSortOrder)

        // When
        val actualSortOrder = storageRepository.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(expectedSortOrder)

    }

    @Test
    fun `default sort order is returned when there is no saved sort order`() = runTest {

        // When
        val actualSortOrder = storageRepository.getSortOrder()

        // Then
        Truth.assertThat(actualSortOrder).isEqualTo(LocalStorage.DEFAULT_SORT_ORDER)

    }

    @Test
    fun `sort order flow emits the saved sort order`() = runTest {

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
        val expectedSortOrders = listOf(
            LocalStorage.DEFAULT_SORT_ORDER,
            SortOrder.PINNED_FIRST,
            SortOrder.Z_TO_A,
            SortOrder.A_TO_Z
        )
        Truth.assertThat(actualSortOrders).isEqualTo(expectedSortOrders)

        job.cancel()

    }

    @Test
    fun `last update check date is saved correctly`() = runTest {

        // Given
        val expectedLastUpdateCheckDate = "15:30, March 2022"

        // When
        storageRepository.saveLastUpdateCheck(expectedLastUpdateCheckDate)

        // Then
        Truth.assertThat(fakeStorage.getLastUpdateCheckAsFlow().first())
            .isEqualTo(expectedLastUpdateCheckDate)

    }

    @Test
    fun `last update check date flow emits the saved last update check date`() = runTest {

        // Given
        val actualUpdateCheckDates = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher()) {
            storageRepository.getLastUpdateCheckAsFlow().toList(actualUpdateCheckDates)
        }

        // When
        fakeStorage.saveLastUpdateCheck("12:44, January 5 2023")
        fakeStorage.saveLastUpdateCheck("18:20, January 5 2023")

        // Then
        val expectedUpdateCheckDates = listOf(
            LocalStorage.DEFAULT_LAST_UPDATE_CHECK, "12:44, January 5 2023", "18:20, January 5 2023"
        )
        Truth.assertThat(actualUpdateCheckDates).isEqualTo(expectedUpdateCheckDates)

        job.cancel()

    }

}
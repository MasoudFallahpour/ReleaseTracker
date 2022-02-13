package ir.fallahpoor.releasetracker.addlibrary.view

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.fakes.FakeLibraryRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class AddLibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun screen_is_initialized_correctly() {

        // Given
        initializeAddLibraryScreen()

        // When the screen is initially displayed

        // Then
        with(composeTestRule) {
            onNodeWithTag(AddLibraryTags.TITLE)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.BACK_BUTTON)
                .assertIsDisplayed()
            onNodeWithTag(AddLibraryTags.CONTENT)
                .assertIsDisplayed()
        }

    }

    @Test
    fun correct_callback_is_called_when_pressing_the_back_button() {

        // Given
        val onBackClick: () -> Unit = mock()
        initializeAddLibraryScreen(onBackClick = onBackClick)

        // When
        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back))
            .performClick()

        // Then
        Mockito.verify(onBackClick).invoke()

    }

    private fun initializeAddLibraryScreen(onBackClick: () -> Unit = {}) {
        composeTestRule.setContent {
            AddLibraryScreen(
                addLibraryViewModel = AddLibraryViewModel(
                    FakeLibraryRepository(),
                    ExceptionParser()
                ),
                isDarkTheme = false,
                onBackClick = onBackClick
            )
        }
    }

}
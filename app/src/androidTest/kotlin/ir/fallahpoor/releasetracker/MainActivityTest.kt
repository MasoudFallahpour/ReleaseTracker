package ir.fallahpoor.releasetracker

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import ir.fallahpoor.releasetracker.addlibrary.ui.AddLibraryTags
import ir.fallahpoor.releasetracker.libraries.ui.LibrariesListContentTags
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalAnimationApi::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

//    @get:Rule(order = 2)
//    var rule = ActivityScenarioRule(MainActivity::class.java)

    // FIXME: the following test sometimes fails and sometimes passes.
//    @Test
//    fun clicking_a_library_opens_its_url_in_a_browser() {
//
//        Intents.init()
//
//        // When
//        composeRule.onNodeWithText(FakeLibraryRepository.Coil.name, useUnmergedTree = true)
//            .performClick()
//
//        // Then
//        intended(
//            allOf(
//                hasAction(Intent.ACTION_VIEW),
//                hasData(FakeLibraryRepository.Coil.url)
//            )
//        )
//
//        Intents.release()
//
//    }

    @Test
    fun when_add_library_button_is_clicked_add_library_screen_is_displayed() {

        // When
        composeRule.onNodeWithTag(LibrariesListContentTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        composeRule.onNodeWithTag(AddLibraryTags.SCREEN)
            .assertIsDisplayed()

    }

}
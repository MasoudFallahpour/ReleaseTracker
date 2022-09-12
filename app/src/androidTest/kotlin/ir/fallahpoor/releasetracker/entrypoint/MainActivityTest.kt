package ir.fallahpoor.releasetracker.entrypoint

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import ir.fallahpoor.releasetracker.features.addlibrary.ui.AddLibraryScreenTags
import ir.fallahpoor.releasetracker.features.libraries.ui.LibrariesListTags
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
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
        composeRule.onNodeWithTag(LibrariesListTags.ADD_LIBRARY_BUTTON)
            .performClick()

        // Then
        composeRule.onNodeWithTag(AddLibraryScreenTags.SCREEN)
            .assertIsDisplayed()

    }

}
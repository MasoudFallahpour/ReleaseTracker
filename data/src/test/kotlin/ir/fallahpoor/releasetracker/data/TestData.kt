package ir.fallahpoor.releasetracker.data

import ir.fallahpoor.releasetracker.data.entity.Library

object TestData {

    private const val GITHUB_BASE_URL = "https://github.com"

    const val OWNER_1 = "masoodfallahpoor"
    const val VERSION_1 = "0.2"
    const val TAG_NAME_1 = "v0.2"
    const val LIBRARY_NAME_1 = "ReleaseTracker"
    const val LIBRARY_URL_1 = "$GITHUB_BASE_URL/$OWNER_1/$LIBRARY_NAME_1"
    const val LIBRARY_VERSION_1 = "0.2"
    val TEST_LIBRARY_1 = Library(LIBRARY_NAME_1, LIBRARY_URL_1, LIBRARY_VERSION_1)

    const val OWNER_2 = "coil-kt"
    const val TAG_NAME_2 = "v1.0"
    const val LIBRARY_NAME_2 = "Coil"
    const val LIBRARY_URL_2 = "$GITHUB_BASE_URL/$OWNER_2/$LIBRARY_NAME_2"
    const val LIBRARY_VERSION_2 = "1.0"
    val TEST_LIBRARY_2 = Library(LIBRARY_NAME_2, LIBRARY_URL_2, LIBRARY_VERSION_2)

    val TEST_LIBRARY_3 = Library("Timber", "https://github.com/JakeWharton/timber", "4.7")

}
package ir.fallahpoor.releasetracker.data

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity

object TestData {

    private const val GITHUB_BASE_URL = "https://github.com"

    const val OWNER_1 = "masoodfallahpoor"
    const val VERSION_1 = "0.2"
    const val TAG_NAME_1 = "v0.2"
    const val LIBRARY_NAME_1 = "ReleaseTracker"
    const val LIBRARY_URL_1 = "$GITHUB_BASE_URL/$OWNER_1/$LIBRARY_NAME_1"
    const val LIBRARY_VERSION_1 = "0.2"
    val RELEASE_TRACKER = LibraryEntity(
        name = LIBRARY_NAME_1,
        url = LIBRARY_URL_1,
        version = LIBRARY_VERSION_1,
        pinned = 0
    )

    const val OWNER_2 = "coil-kt"
    const val TAG_NAME_2 = "v1.0"
    const val LIBRARY_NAME_2 = "Coil"
    const val LIBRARY_URL_2 = "$GITHUB_BASE_URL/$OWNER_2/$LIBRARY_NAME_2"
    const val LIBRARY_VERSION_2 = "1.0"
    val COIL = LibraryEntity(
        name = LIBRARY_NAME_2,
        url = LIBRARY_URL_2,
        version = LIBRARY_VERSION_2,
        pinned = 0
    )

    val TIMBER = LibraryEntity(
        name = "Timber",
        url = "https://github.com/JakeWharton/timber",
        version = "4.7",
        pinned = 0
    )

}
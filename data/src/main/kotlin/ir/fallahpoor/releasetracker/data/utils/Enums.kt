package ir.fallahpoor.releasetracker.data.utils

enum class NightMode(val value: String) {
    ON("On"),
    OFF("Off"),
    AUTO("Auto")
}

enum class SortOrder {
    A_TO_Z,
    Z_TO_A,
    PINNED_FIRST
}

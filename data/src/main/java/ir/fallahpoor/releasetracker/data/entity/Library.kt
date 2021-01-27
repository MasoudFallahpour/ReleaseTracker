package ir.fallahpoor.releasetracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.fallahpoor.releasetracker.data.database.DatabaseContract

@Entity
data class Library(
    @PrimaryKey
    @ColumnInfo(name = DatabaseContract.FIELD_NAME, collate = ColumnInfo.NOCASE)
    val name: String,
    @ColumnInfo(name = DatabaseContract.FIELD_URL)
    val url: String,
    @ColumnInfo(name = DatabaseContract.FIELD_VERSION)
    val version: String,
    @ColumnInfo(name = DatabaseContract.FIELD_PINNED)
    val pinned: Int = 0
) {

    fun isPinned() = pinned != 0

}
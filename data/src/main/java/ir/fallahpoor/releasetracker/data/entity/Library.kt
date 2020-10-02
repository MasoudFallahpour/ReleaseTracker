package ir.fallahpoor.releasetracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Library(
    @PrimaryKey
    @ColumnInfo(name = "name", collate = ColumnInfo.NOCASE)
    val libraryName: String,
    @ColumnInfo(name = "url")
    val libraryUrl: String,
    @ColumnInfo(name = "version")
    val version: String
)
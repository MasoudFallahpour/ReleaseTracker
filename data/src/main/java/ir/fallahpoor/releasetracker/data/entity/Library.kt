package ir.fallahpoor.releasetracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["name"])
data class Library(
    @ColumnInfo(name = "name")
    val libraryName: String,
    @ColumnInfo(name = "url")
    val libraryUrl: String
)
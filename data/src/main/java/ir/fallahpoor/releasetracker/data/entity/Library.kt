package ir.fallahpoor.releasetracker.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Library(
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val name: String,
    val url: String,
    val version: String,
    val pinned: Int = 0
)
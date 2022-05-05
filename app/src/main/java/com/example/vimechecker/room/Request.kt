package com.example.vimechecker.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_table")
data class Request (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "rid") val rid: Int?,
    @ColumnInfo(name = "avatar_url") val avatar: String?,
    @ColumnInfo(name = "ranK_tag") val rankTag: String?,
    @ColumnInfo(name = "level") val level: Int?,
    @ColumnInfo(name = "type") val type: String?
)





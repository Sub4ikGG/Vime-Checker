package com.example.vimechecker.model.playerOnline

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PlayerOnlineItem(
    val guild: @RawValue Guild? = null,
    val id: @RawValue Int,
    val lastSeen: @RawValue Int,
    val level: @RawValue Int,
    val levelPercentage: @RawValue Double,
    val online: @RawValue Online,
    val playedSeconds: @RawValue Int,
    val rank: @RawValue String,
    var username: @RawValue String
): Parcelable
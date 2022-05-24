package com.gifapplication.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "gif_table")
data class Gif(
    @PrimaryKey
    var id : String,
    var type : String? = null,
    var url : String? = null,
    var slug : String? = null,
    var bitly_gif_url : String? = null,
    var bitly_url : String? = null,
    var username : String? = null,
    var title : String? = null,
    var images : Images? = null
): Parcelable

@Parcelize
data class Images(
    var original : Original? = null
): Parcelable

@Parcelize
data class Original(
    var url: String? = null
): Parcelable
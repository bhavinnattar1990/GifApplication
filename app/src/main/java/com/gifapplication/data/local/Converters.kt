package com.gifapplication.data.local

import androidx.room.TypeConverter
import com.gifapplication.data.model.Images
import com.gifapplication.data.model.Original

class Converters {

    @TypeConverter
    fun fromImages(images: Images): String{
        return images?.original?.url.toString()
    }

    @TypeConverter
    fun toImages(url: String): Images{
        return Images(Original(url))
    }
}
package com.example.wardrobeassistant.data.local

import androidx.room.TypeConverter
import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season

// Room не умеет сам хранить наши enum
// поэтому конвертим их в строки и обратно
class Converters {

    // категория
    @TypeConverter
    fun categoryToString(value: Category): String {
        return value.name
    }

    @TypeConverter
    fun stringToCategory(value: String): Category {
        return Category.valueOf(value)
    }

    // цвет
    @TypeConverter
    fun colorToString(value: ColorGroup): String {
        return value.name
    }

    @TypeConverter
    fun stringToColor(value: String): ColorGroup {
        return ColorGroup.valueOf(value)
    }

    // сезон
    @TypeConverter
    fun seasonToString(value: Season): String {
        return value.name
    }

    @TypeConverter
    fun stringToSeason(value: String): Season {
        return Season.valueOf(value)
    }
}

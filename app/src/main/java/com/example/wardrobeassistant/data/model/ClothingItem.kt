package com.example.wardrobeassistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room будет хранить вещи в таблице clothing_items
@Entity(tableName = "clothing_items")
data class ClothingItem(

    // id будет генерироваться автоматически
    // ставим 0 при добавлении новой вещи
    // тогда Room сам подставит реальный id
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val category: Category,
    val color: ColorGroup,
    val season: Season,

    // путь к выбранной фотографии
    // null если фото не выбрано
    val imageUri: String? = null
)

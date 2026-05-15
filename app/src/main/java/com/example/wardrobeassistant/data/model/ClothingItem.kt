package com.example.wardrobeassistant.data.model

data class ClothingItem(
    val id: Int,
    val name: String,
    val category: Category,
    val color: ColorGroup,
    val season: Season,
    // путь к выбранной фотографии
    // null если фото не выбрано
    val imageUri: String? = null
)

package com.example.wardrobeassistant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season

class WardrobeViewModel : ViewModel() {

    // счетчик для id вещей
    // пока сделал просто вручную
    private var nextId = 1

    // основной список одежды
    // mutableStateListOf нужен чтобы compose видел изменения
    val clothingItems = mutableStateListOf<ClothingItem>()

    fun addClothingItem(
        name: String,
        category: Category,
        color: ColorGroup,
        season: Season
    ) {

        // защита от пустого названия
        // потом можно будет добавить snackbar или ошибку
        if (name.isBlank()) {
            return
        }

        // создаем новый объект одежды
        val item = ClothingItem(
            id = nextId++,
            name = name.trim(),
            category = category,
            color = color,
            season = season
        )

        // добавляем в список
        clothingItems.add(item)
    }

    // удаление вещи по id
    // ищем по id чтобы случайно не удалить не ту
    fun removeClothingItem(id: Int) {

        // removeAll вернет true если что то удалилось
        // но нам это значение не нужно
        clothingItems.removeAll { item ->
            item.id == id
        }
    }
}
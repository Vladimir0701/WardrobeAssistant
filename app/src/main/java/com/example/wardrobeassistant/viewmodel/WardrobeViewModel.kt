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

    // тестовые данные при первом запуске
    // init вызывается один раз при создании viewmodel
    // потом уберем когда подключим Room
    init {

        addClothingItem(
            name = "Черная футболка",
            category = Category.BASE_TOP,
            color = ColorGroup.BLACK,
            season = Season.SUMMER,
            imageUri = null
        )

        addClothingItem(
            name = "Синие джинсы",
            category = Category.BOTTOM,
            color = ColorGroup.BLUE,
            season = Season.DEMI_SEASON,
            imageUri = null
        )
    }

    fun addClothingItem(
        name: String,
        category: Category,
        color: ColorGroup,
        season: Season,
        imageUri: String?
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
            season = season,
            imageUri = imageUri
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

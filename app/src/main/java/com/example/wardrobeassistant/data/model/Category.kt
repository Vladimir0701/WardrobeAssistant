package com.example.wardrobeassistant.data.model

// у каждой категории есть русское имя для интерфейса
enum class Category(val displayName: String) {
    BASE_TOP("Базовый верх"),
    OUTER_LAYER("Верхний слой"),
    BOTTOM("Низ"),
    ACCESSORY("Аксессуары")
}

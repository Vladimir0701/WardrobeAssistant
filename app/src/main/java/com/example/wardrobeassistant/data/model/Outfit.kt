package com.example.wardrobeassistant.data.model

// один сгенерированный комплект
// items - что в комплекте (базовый верх + низ + опц. верхний слой)
// score - оценка совместимости от 0.0 до 1.0
data class Outfit(
    val items: List<ClothingItem>,
    val score: Double
)

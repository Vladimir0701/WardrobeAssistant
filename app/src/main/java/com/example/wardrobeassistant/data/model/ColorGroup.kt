package com.example.wardrobeassistant.data.model

import androidx.compose.ui.graphics.Color

// у цвета есть имя на русском и реальный цвет
// для отображения в палитре
enum class ColorGroup(
    val displayName: String,
    val color: Color
) {
    BLACK("Чёрный", Color(0xFF000000)),
    WHITE("Белый", Color(0xFFFFFFFF)),
    GRAY("Серый", Color(0xFF9E9E9E)),
    BEIGE("Бежевый", Color(0xFFD7C9A7)),
    BROWN("Коричневый", Color(0xFF8D6E63)),
    RED("Красный", Color(0xFFD32F2F)),
    PINK("Розовый", Color(0xFFE91E63)),
    ORANGE("Оранжевый", Color(0xFFF57C00)),
    YELLOW("Жёлтый", Color(0xFFFBC02D)),
    GREEN("Зелёный", Color(0xFF388E3C)),
    BLUE("Синий", Color(0xFF1976D2)),
    LIGHT_BLUE("Голубой", Color(0xFF03A9F4)),
    PURPLE("Фиолетовый", Color(0xFF7B1FA2))
}

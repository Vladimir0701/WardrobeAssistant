package com.example.wardrobeassistant.data.model

import androidx.compose.ui.graphics.Color

// у цвета есть имя на русском, реальный цвет для палитры
// и позиция на 12 цветовом круге Иттена (0..11)
// нейтральные цвета (черный, белый и тд) - вне круга
enum class ColorGroup(
    val displayName: String,
    val color: Color,
    val wheelPosition: Int?,
    val isNeutral: Boolean
) {
    // нейтральные (ахроматические) - сочетаются почти со всем
    BLACK("Чёрный", Color(0xFF000000), null, true),
    WHITE("Белый", Color(0xFFFFFFFF), null, true),
    GRAY("Серый", Color(0xFF9E9E9E), null, true),
    BEIGE("Бежевый", Color(0xFFD7C9A7), null, true),
    BROWN("Коричневый", Color(0xFF8D6E63), null, true),

    // хроматические - на круге Иттена
    // позиции 0..11 по часовой стрелке начиная с красного
    RED("Красный", Color(0xFFD32F2F), 0, false),
    ORANGE("Оранжевый", Color(0xFFF57C00), 2, false),
    YELLOW("Жёлтый", Color(0xFFFBC02D), 4, false),
    GREEN("Зелёный", Color(0xFF388E3C), 6, false),
    LIGHT_BLUE("Голубой", Color(0xFF03A9F4), 7, false),
    BLUE("Синий", Color(0xFF1976D2), 8, false),
    PURPLE("Фиолетовый", Color(0xFF7B1FA2), 10, false),
    PINK("Розовый", Color(0xFFE91E63), 11, false)
}

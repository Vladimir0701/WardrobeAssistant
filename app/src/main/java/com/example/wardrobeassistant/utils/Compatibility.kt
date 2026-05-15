package com.example.wardrobeassistant.utils

import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season
import kotlin.math.abs
import kotlin.math.min

// экспертные правила совместимости одежды
// основаны на цветовом круге Иттена и сезонности
// score 0.0 - вообще не сочетается
// score 1.0 - идеально

// оценка совместимости двух цветов
fun colorScore(a: ColorGroup, b: ColorGroup): Double {

    // два нейтральных (черный+белый и тд) - идеально
    if (a.isNeutral && b.isNeutral) {
        return 1.0
    }

    // один из цветов нейтральный - хорошо сочетается со всем
    if (a.isNeutral || b.isNeutral) {
        return 0.9
    }

    // один и тот же цвет - идеально (монохром)
    if (a == b) {
        return 1.0
    }

    // оба хроматические - считаем дистанцию на круге Иттена
    // wheelPosition не null т.к. оба не нейтральные
    val pos1 = a.wheelPosition!!
    val pos2 = b.wheelPosition!!

    // дистанция по кратчайшему пути на круге
    // круг из 12 позиций - максимальная дистанция 6
    val raw = abs(pos1 - pos2)
    val distance = min(raw, 12 - raw)

    // классические схемы сочетаемости по Иттену
    return when (distance) {
        0 -> 1.0   // монохром
        1 -> 0.85  // аналогичные соседние
        2 -> 0.80  // аналогичные через одного
        3 -> 0.50  // не входит в классические схемы
        4 -> 0.75  // триадическое (3 цвета через 120 градусов)
        5 -> 0.55  // split-комплементарное
        6 -> 0.90  // комплементарное (противоположные)
        else -> 0.5
    }
}

// оценка совместимости двух сезонов
fun seasonScore(a: Season, b: Season): Double {

    // одинаковый сезон - идеально
    if (a == b) {
        return 1.0
    }

    // демисезон сочетается с любым сезоном
    if (a == Season.DEMI_SEASON || b == Season.DEMI_SEASON) {
        return 1.0
    }

    // лето + зима - плохое сочетание
    // разные ткани, разная погода
    return 0.3
}

// оценка совместимости двух конкретных вещей
// учитывает и цвет и сезон
fun pairScore(a: ClothingItem, b: ClothingItem): Double {

    val color = colorScore(a.color, b.color)
    val season = seasonScore(a.season, b.season)

    // перемножаем чтобы плохой сезон ронял общую оценку
    // даже при идеальных цветах
    return color * season
}

// оценка комплекта целиком
// берем все пары вещей и считаем среднее
fun outfitScore(items: List<ClothingItem>): Double {

    // комплект из одной или нуля вещей - бессмысленно
    if (items.size < 2) {
        return 0.0
    }

    // собираем все возможные пары
    val scores = mutableListOf<Double>()

    for (i in items.indices) {
        for (j in i + 1 until items.size) {
            scores.add(pairScore(items[i], items[j]))
        }
    }

    // среднее арифметическое
    return scores.average()
}

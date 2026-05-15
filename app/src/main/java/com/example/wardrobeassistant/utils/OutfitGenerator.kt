package com.example.wardrobeassistant.utils

import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.Outfit

// генератор комплектов одежды
// проходит по всем разумным комбинациям и считает оценку

fun generateOutfits(items: List<ClothingItem>): List<Outfit> {

    // разбиваем гардероб по категориям
    val bases = items.filter { it.category == Category.BASE_TOP }
    val bottoms = items.filter { it.category == Category.BOTTOM }
    val outers = items.filter { it.category == Category.OUTER_LAYER }

    // если нет базового верха или низа - комплект не собрать
    if (bases.isEmpty() || bottoms.isEmpty()) {
        return emptyList()
    }

    val outfits = mutableListOf<Outfit>()

    // перебираем все комбинации база x низ
    for (base in bases) {
        for (bottom in bottoms) {

            // комплект из двух вещей
            val pair = listOf(base, bottom)
            outfits.add(
                Outfit(
                    items = pair,
                    score = outfitScore(pair)
                )
            )

            // тот же база+низ но с верхним слоем
            // получится отдельный комплект на каждый верхний слой
            for (outer in outers) {
                val triple = listOf(base, bottom, outer)
                outfits.add(
                    Outfit(
                        items = triple,
                        score = outfitScore(triple)
                    )
                )
            }
        }
    }

    // сортируем по убыванию оценки
    // самые удачные сочетания будут сверху
    return outfits.sortedByDescending { it.score }
}

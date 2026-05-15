package com.example.wardrobeassistant

import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season
import com.example.wardrobeassistant.utils.colorScore
import com.example.wardrobeassistant.utils.findBestMatch
import com.example.wardrobeassistant.utils.outfitScore
import com.example.wardrobeassistant.utils.pairScore
import com.example.wardrobeassistant.utils.seasonScore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

// тесты на алгоритм совместимости
// проверяем что круг Иттена и сезонные правила работают
// как мы задумали
class CompatibilityTest {

    // вспомогательная функция чтобы быстро делать тестовые вещи
    private fun item(
        id: Int = 1,
        category: Category = Category.BASE_TOP,
        color: ColorGroup = ColorGroup.BLACK,
        season: Season = Season.DEMI_SEASON
    ): ClothingItem {
        return ClothingItem(
            id = id,
            name = "test",
            category = category,
            color = color,
            season = season,
            imageUri = null
        )
    }

    // ---------- colorScore ----------

    @Test
    fun testColorMatch_BlackWhite_Perfect() {

        // два нейтральных - всегда 1.0
        val result = colorScore(
            ColorGroup.BLACK,
            ColorGroup.WHITE
        )
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun testColorMatch_NeutralAndChromatic() {

        // один нейтральный + хроматический - 0.9
        val result = colorScore(
            ColorGroup.BLACK,
            ColorGroup.BLUE
        )
        assertEquals(0.9, result, 0.001)
    }

    @Test
    fun testColorMatch_SameColor_Mono() {

        // один и тот же цвет (монохром) - 1.0
        val result = colorScore(
            ColorGroup.RED,
            ColorGroup.RED
        )
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun testColorMatch_Analogous_RedOrange() {

        // соседи на круге (дистанция 2) - аналогичные
        val result = colorScore(
            ColorGroup.RED,
            ColorGroup.ORANGE
        )
        assertEquals(0.8, result, 0.001)
    }

    @Test
    fun testColorMatch_Triadic_RedYellow() {

        // дистанция 4 на круге - триадическое сочетание
        val result = colorScore(
            ColorGroup.RED,
            ColorGroup.YELLOW
        )
        assertEquals(0.75, result, 0.001)
    }

    @Test
    fun testColorMatch_Complementary_RedGreen() {

        // дистанция 6 (противоположные) - комплементарное
        val result = colorScore(
            ColorGroup.RED,
            ColorGroup.GREEN
        )
        assertEquals(0.9, result, 0.001)
    }

    @Test
    fun testColorMatch_NoScheme_PinkOrange() {

        // pink=11, orange=2, дистанция 3
        // не входит ни в одну классическую схему
        val result = colorScore(
            ColorGroup.PINK,
            ColorGroup.ORANGE
        )
        assertEquals(0.5, result, 0.001)
    }

    @Test
    fun testColorMatch_WrapAround_RedPink() {

        // red=0, pink=11, но по короткому пути дистанция 1
        // проверяем что обход по кругу работает правильно
        val result = colorScore(
            ColorGroup.RED,
            ColorGroup.PINK
        )
        assertEquals(0.85, result, 0.001)
    }

    // ---------- seasonScore ----------

    @Test
    fun testSeason_SameSeason() {

        val result = seasonScore(
            Season.SUMMER,
            Season.SUMMER
        )
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun testSeason_DemiUniversal() {

        // демисезон сочетается с любым
        val result = seasonScore(
            Season.SUMMER,
            Season.DEMI_SEASON
        )
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun testSeason_SummerWinter_Bad() {

        // зима + лето - плохое сочетание
        val result = seasonScore(
            Season.SUMMER,
            Season.WINTER
        )
        assertEquals(0.3, result, 0.001)
    }

    // ---------- pairScore ----------

    @Test
    fun testPairScore_PerfectColorBadSeason() {

        // черный + белый цвета (1.0)
        // но лето + зима (0.3)
        // итого 1.0 * 0.3 = 0.3
        val summer = item(
            color = ColorGroup.BLACK,
            season = Season.SUMMER
        )
        val winter = item(
            id = 2,
            color = ColorGroup.WHITE,
            season = Season.WINTER
        )

        val result = pairScore(summer, winter)
        assertEquals(0.3, result, 0.001)
    }

    // ---------- outfitScore ----------

    @Test
    fun testOutfitScore_TooFewItems() {

        // одна вещь или ноль - комплекта нет
        assertEquals(0.0, outfitScore(emptyList()), 0.001)
        assertEquals(0.0, outfitScore(listOf(item())), 0.001)
    }

    @Test
    fun testOutfitScore_ThreeItems_AllNeutrals() {

        // три нейтральные вещи в демисезон - идеально
        val a = item(id = 1, color = ColorGroup.BLACK)
        val b = item(id = 2, color = ColorGroup.WHITE)
        val c = item(id = 3, color = ColorGroup.GRAY)

        val result = outfitScore(listOf(a, b, c))
        assertEquals(1.0, result, 0.001)
    }

    // ---------- findBestMatch ----------

    @Test
    fun testFindBestMatch_EmptyPool() {

        // нет кандидатов - возвращаем null
        val result = findBestMatch(
            pool = emptyList(),
            filledItems = emptyList()
        )
        assertNull(result)
    }

    @Test
    fun testFindBestMatch_PicksBest() {

        // у нас есть белая футболка летом
        // надо выбрать низ который ей подойдет
        val target = item(
            id = 1,
            color = ColorGroup.WHITE,
            season = Season.SUMMER
        )

        // кандидат 1: черные джинсы летом
        // нейтральный + одинаковый сезон = 0.9 * 1.0 = 0.9
        val goodCandidate = item(
            id = 2,
            category = Category.BOTTOM,
            color = ColorGroup.BLACK,
            season = Season.SUMMER
        )

        // кандидат 2: красные штаны зимой
        // нейтральный + конфликт сезона = 0.9 * 0.3 = 0.27
        val badCandidate = item(
            id = 3,
            category = Category.BOTTOM,
            color = ColorGroup.RED,
            season = Season.WINTER
        )

        val result = findBestMatch(
            pool = listOf(badCandidate, goodCandidate),
            filledItems = listOf(target)
        )

        assertNotNull(result)
        assertEquals(goodCandidate.id, result?.id)
    }
}

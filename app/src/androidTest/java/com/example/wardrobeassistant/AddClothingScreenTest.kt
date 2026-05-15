package com.example.wardrobeassistant

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wardrobeassistant.ui.screens.AddClothingScreen
import com.example.wardrobeassistant.ui.theme.WardrobeAssistantTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// UI тест экрана добавления одежды
// проверяем что экран рисуется и что сохранение работает
@RunWith(AndroidJUnit4::class)
class AddClothingScreenTest {

    // правило поднимает Activity и дает доступ к compose дереву
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testAddClothingScreen_ShowsElements() {

        // рисуем экран без существующей вещи (режим добавления)
        composeRule.setContent {
            WardrobeAssistantTheme {
                AddClothingScreen(
                    onSaveClick = { _, _, _, _, _ -> }
                )
            }
        }

        // проверяем что ключевые элементы на месте
        composeRule
            .onNodeWithText("Добавление одежды")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Название")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Сохранить")
            .assertIsDisplayed()
    }

    @Test
    fun testAddClothing_Success() {

        // сюда упадет имя которое передали в onSaveClick
        var savedName = ""

        composeRule.setContent {
            WardrobeAssistantTheme {
                AddClothingScreen(
                    onSaveClick = { name, _, _, _, _ ->
                        savedName = name
                    }
                )
            }
        }

        // вводим название в поле
        composeRule
            .onNodeWithText("Название")
            .performTextInput("Тестовая футболка")

        // жмем сохранить
        composeRule
            .onNodeWithText("Сохранить")
            .performClick()

        // ждем пока compose обработает и проверяем
        composeRule.runOnIdle {
            assertEquals("Тестовая футболка", savedName)
        }
    }
}

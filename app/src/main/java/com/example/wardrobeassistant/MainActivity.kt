package com.example.wardrobeassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wardrobeassistant.ui.screens.AddClothingScreen
import com.example.wardrobeassistant.ui.screens.WardrobeScreen
import com.example.wardrobeassistant.ui.theme.WardrobeAssistantTheme
import com.example.wardrobeassistant.viewmodel.WardrobeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            // тема приложения
            WardrobeAssistantTheme {

                // viewModel со списком одежды
                val wardrobeViewModel: WardrobeViewModel = viewModel()

                // состояние текущего экрана
                // false = гардероб
                // true = экран добавления
                var isAddingScreenVisible by remember {
                    mutableStateOf(false)
                }

                Column(

                    // небольшой отступ сверху
                    // чтобы кнопка не залезала под статус бар
                    modifier = Modifier.padding(top = 40.dp)
                ) {

                    // кнопка переключения экранов
                    Button(
                        onClick = {

                            // меняем текущий экран
                            isAddingScreenVisible =
                                !isAddingScreenVisible
                        }
                    ) {

                        if (isAddingScreenVisible) {

                            Text("К гардеробу")

                        } else {

                            Text("Добавить одежду")
                        }
                    }

                    // экран добавления одежды
                    if (isAddingScreenVisible) {

                        AddClothingScreen(

                            onSaveClick = {
                                    name,
                                    category,
                                    color,
                                    season ->

                                // добавляем новую вещь
                                wardrobeViewModel.addClothingItem(
                                    name = name,
                                    category = category,
                                    color = color,
                                    season = season
                                )

                                // после сохранения возвращаемся
                                // обратно к гардеробу
                                isAddingScreenVisible = false
                            }
                        )

                    } else {

                        // основной экран гардероба
                        WardrobeScreen(
                            clothingItems =
                                wardrobeViewModel.clothingItems,

                            onDeleteClick = { item ->

                                // удаляем выбранную вещь
                                wardrobeViewModel
                                    .removeClothingItem(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
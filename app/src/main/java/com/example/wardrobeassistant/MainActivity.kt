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
import com.example.wardrobeassistant.data.model.ClothingItem
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

                // флаг что открыт экран добавления новой вещи
                var isAddingScreenVisible by remember {
                    mutableStateOf(false)
                }

                // если не null - значит редактируем эту вещь
                // null - значит редактирование не открыто
                var editingItem: ClothingItem? by remember {
                    mutableStateOf(null)
                }

                // показываем форму (добавление или редактирование)
                // если хотя бы один из флагов установлен
                val isFormVisible =
                    isAddingScreenVisible || editingItem != null

                Column(

                    // небольшой отступ сверху
                    // чтобы кнопка не залезала под статус бар
                    modifier = Modifier.padding(top = 40.dp)
                ) {

                    // кнопка переключения экранов
                    Button(
                        onClick = {

                            if (isFormVisible) {

                                // выходим из формы
                                isAddingScreenVisible = false
                                editingItem = null

                            } else {

                                // открываем экран добавления
                                isAddingScreenVisible = true
                            }
                        }
                    ) {

                        if (isFormVisible) {

                            Text("К гардеробу")

                        } else {

                            Text("Добавить одежду")
                        }
                    }

                    if (isFormVisible) {

                        // запоминаем текущий редактируемый
                        // в локальной val чтобы умный каст работал внутри лямбды
                        val currentlyEditing = editingItem

                        AddClothingScreen(

                            existingItem = currentlyEditing,

                            onSaveClick = {
                                    name,
                                    category,
                                    color,
                                    season,
                                    imageUri ->

                                if (currentlyEditing == null) {

                                    // добавляем новую вещь
                                    wardrobeViewModel.addClothingItem(
                                        name = name,
                                        category = category,
                                        color = color,
                                        season = season,
                                        imageUri = imageUri
                                    )

                                } else {

                                    // обновляем существующую
                                    wardrobeViewModel.updateClothingItem(
                                        id = currentlyEditing.id,
                                        name = name,
                                        category = category,
                                        color = color,
                                        season = season,
                                        imageUri = imageUri
                                    )
                                }

                                // после сохранения возвращаемся
                                // обратно к гардеробу
                                isAddingScreenVisible = false
                                editingItem = null
                            }
                        )

                    } else {

                        // основной экран гардероба
                        WardrobeScreen(
                            clothingItems =
                                wardrobeViewModel.clothingItems,

                            onEditClick = { item ->

                                // открываем форму редактирования
                                editingItem = item
                            },

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

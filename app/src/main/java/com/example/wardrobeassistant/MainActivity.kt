package com.example.wardrobeassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.ui.screens.AddClothingScreen
import com.example.wardrobeassistant.ui.screens.OutfitsScreen
import com.example.wardrobeassistant.ui.screens.WardrobeScreen
import com.example.wardrobeassistant.ui.theme.WardrobeAssistantTheme
import com.example.wardrobeassistant.utils.requestSubjectSegmentationModel
import com.example.wardrobeassistant.viewmodel.WardrobeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // запрашиваем установку модели сегментации сразу при старте
        // первый раз скачивание может занять минуту-две
        requestSubjectSegmentationModel(this)

        setContent {

            // тема приложения
            WardrobeAssistantTheme {

                // viewModel со списком одежды
                val wardrobeViewModel: WardrobeViewModel = viewModel()

                // подписываемся на список из БД
                val clothingItems by wardrobeViewModel
                    .clothingItems
                    .collectAsState()

                // флаг что открыт экран добавления новой вещи
                var isAddingScreenVisible by remember {
                    mutableStateOf(false)
                }

                // если не null - значит редактируем эту вещь
                var editingItem: ClothingItem? by remember {
                    mutableStateOf(null)
                }

                // флаг что открыт экран подбора комплектов
                var isOutfitsScreenVisible by remember {
                    mutableStateOf(false)
                }

                // показываем форму (добавление или редактирование)
                val isFormVisible =
                    isAddingScreenVisible || editingItem != null

                // мы где то кроме главного экрана гардероба
                val isOnSubScreen = isFormVisible || isOutfitsScreenVisible

                Column(

                    // небольшой отступ сверху
                    // чтобы кнопка не залезала под статус бар
                    modifier = Modifier.padding(
                        top = 40.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                ) {

                    if (isOnSubScreen) {

                        // на любом подэкране - кнопка вернуться
                        Button(
                            onClick = {

                                // сбрасываем все флаги
                                isAddingScreenVisible = false
                                editingItem = null
                                isOutfitsScreenVisible = false
                            }
                        ) {
                            Text("К гардеробу")
                        }

                    } else {

                        // на главном экране - две кнопки в ряд
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {
                                    isAddingScreenVisible = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Добавить")
                            }

                            Button(
                                onClick = {
                                    isOutfitsScreenVisible = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Подобрать комплект")
                            }
                        }
                    }

                    when {

                        isFormVisible -> {

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

                                        wardrobeViewModel.addClothingItem(
                                            name = name,
                                            category = category,
                                            color = color,
                                            season = season,
                                            imageUri = imageUri
                                        )

                                    } else {

                                        wardrobeViewModel.updateClothingItem(
                                            id = currentlyEditing.id,
                                            name = name,
                                            category = category,
                                            color = color,
                                            season = season,
                                            imageUri = imageUri
                                        )
                                    }

                                    isAddingScreenVisible = false
                                    editingItem = null
                                }
                            )
                        }

                        isOutfitsScreenVisible -> {

                            OutfitsScreen(
                                clothingItems = clothingItems
                            )
                        }

                        else -> {

                            WardrobeScreen(
                                clothingItems = clothingItems,

                                onEditClick = { item ->
                                    editingItem = item
                                },

                                onDeleteClick = { item ->
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
}

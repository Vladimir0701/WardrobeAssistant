package com.example.wardrobeassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // запрашиваем установку модели сегментации сразу при старте
        // первый раз скачивание может занять минуту-две
        requestSubjectSegmentationModel(this)

        setContent {

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
                val isOnSubScreen =
                    isFormVisible || isOutfitsScreenVisible

                // заголовок в шапке зависит от текущего экрана
                val screenTitle = when {
                    editingItem != null -> "Редактирование"
                    isAddingScreenVisible -> "Добавление одежды"
                    isOutfitsScreenVisible -> "Подбор комплекта"
                    else -> "Гардероб"
                }

                Scaffold(
                    topBar = {

                        TopAppBar(
                            title = {
                                Text(screenTitle)
                            },
                            navigationIcon = {

                                // стрелка назад только на подэкранах
                                if (isOnSubScreen) {

                                    IconButton(
                                        onClick = {
                                            // сбрасываем все флаги
                                            isAddingScreenVisible = false
                                            editingItem = null
                                            isOutfitsScreenVisible = false
                                        }
                                    ) {
                                        // стрелка символом
                                        // чтобы не тянуть либу с иконками
                                        Text(
                                            text = "←",
                                            style = MaterialTheme
                                                .typography.titleLarge
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {

                        // нижняя панель с действиями
                        // только на главном экране гардероба
                        if (!isOnSubScreen) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
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
                    }
                ) { innerPadding ->

                    // Box с отступами от Scaffold
                    // innerPadding учитывает шапку, нижнюю панель,
                    // статус бар и вырезы - больше не хардкодим 40.dp
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

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

                                            wardrobeViewModel
                                                .addClothingItem(
                                                    name = name,
                                                    category = category,
                                                    color = color,
                                                    season = season,
                                                    imageUri = imageUri
                                                )

                                        } else {

                                            wardrobeViewModel
                                                .updateClothingItem(
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
}

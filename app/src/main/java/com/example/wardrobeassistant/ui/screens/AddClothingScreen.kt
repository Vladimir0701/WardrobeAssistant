package com.example.wardrobeassistant.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season
import com.example.wardrobeassistant.utils.processAndSaveImage
import com.example.wardrobeassistant.utils.toImageModel
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AddClothingScreen(
    // если передали существующую вещь - значит режим редактирования
    // если null - режим добавления новой
    existingItem: ClothingItem? = null,
    onSaveClick: (
        String,
        Category,
        ColorGroup,
        Season,
        String?
    ) -> Unit
) {

    // ключ для remember чтобы при выборе другой вещи
    // на редактирование поля обновлялись правильно
    val itemKey = existingItem?.id

    // название одежды
    var clothingName by remember(itemKey) {
        mutableStateOf(existingItem?.name ?: "")
    }

    // выбранная категория
    var selectedCategory by remember(itemKey) {
        mutableStateOf(existingItem?.category ?: Category.BASE_TOP)
    }

    // выбранный цвет
    var selectedColor by remember(itemKey) {
        mutableStateOf(existingItem?.color ?: ColorGroup.BLACK)
    }

    // выбранный сезон
    var selectedSeason by remember(itemKey) {
        mutableStateOf(existingItem?.season ?: Season.SUMMER)
    }

    // выбранная фотография
    var selectedImageUri by remember(itemKey) {
        mutableStateOf(existingItem?.imageUri)
    }

    // состояния dropdown меню
    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var seasonExpanded by remember {
        mutableStateOf(false)
    }

    // нужен Context для работы с фото
    val context = LocalContext.current

    // scope для запуска обработки в корутине
    val scope = rememberCoroutineScope()

    // флаг что фото сейчас обрабатывается ML моделью
    // нужен для индикатора загрузки
    var isProcessing by remember {
        mutableStateOf(false)
    }

    // лаунчер для выбора фото из галереи
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null) {

            // запускаем обработку в фоне
            // обработка может занять пару секунд
            scope.launch {

                isProcessing = true

                val savedUri = processAndSaveImage(
                    context = context,
                    sourceUri = uri
                )

                if (savedUri != null) {
                    selectedImageUri = savedUri
                }

                isProcessing = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // пока идет обработка - крутилка вместо превью
        if (isProcessing) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    CircularProgressIndicator()

                    Text(
                        text = "Убираем фон...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        } else if (selectedImageUri != null) {

            // превью выбранной фотографии
            AsyncImage(
                model = toImageModel(selectedImageUri),
                contentDescription = "Превью одежды",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        }

        // кнопка выбора фото
        // блокируем пока обрабатываем чтобы не запустить два раза
        OutlinedButton(
            onClick = {

                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts
                            .PickVisualMedia
                            .ImageOnly
                    )
                )
            },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth()
        ) {

            if (selectedImageUri == null) {
                Text("Выбрать фото")
            } else {
                Text("Заменить фото")
            }
        }

        // поле названия
        OutlinedTextField(
            value = clothingName,
            onValueChange = {
                clothingName = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Название")
            }
        )

        // выбор категории
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = {
                categoryExpanded = !categoryExpanded
            }
        ) {

            OutlinedTextField(
                value = selectedCategory.displayName,
                onValueChange = {},
                readOnly = true,
                // menuAnchor обязателен, без него меню не открывается
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                label = {
                    Text("Категория")
                }
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                }
            ) {

                Category.entries.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(category.displayName)
                        },
                        onClick = {

                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // палитра цветов
        Text(
            text = "Цвет: ${selectedColor.displayName}",
            style = MaterialTheme.typography.bodyLarge
        )

        // FlowRow сам переносит элементы на новую строку
        // если не помещаются по ширине
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            ColorGroup.entries.forEach { color ->

                // кружок выбран если совпадает с текущим
                val isSelected = color == selectedColor

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.color)
                        // у выбранного - толстая темная рамка
                        // у остальных - тонкая серая
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Gray
                            },
                            shape = CircleShape
                        )
                        .clickable {
                            selectedColor = color
                        }
                )
            }
        }

        // выбор сезона
        ExposedDropdownMenuBox(
            expanded = seasonExpanded,
            onExpandedChange = {
                seasonExpanded = !seasonExpanded
            }
        ) {

            OutlinedTextField(
                value = selectedSeason.displayName,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                label = {
                    Text("Сезон")
                }
            )

            ExposedDropdownMenu(
                expanded = seasonExpanded,
                onDismissRequest = {
                    seasonExpanded = false
                }
            ) {

                Season.entries.forEach { season ->

                    DropdownMenuItem(
                        text = {
                            Text(season.displayName)
                        },
                        onClick = {

                            selectedSeason = season
                            seasonExpanded = false
                        }
                    )
                }
            }
        }

        // кнопка сохранения
        // запрещаем сохранять пока идет обработка фото
        Button(
            onClick = {

                onSaveClick(
                    clothingName,
                    selectedCategory,
                    selectedColor,
                    selectedSeason,
                    selectedImageUri
                )
            },
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                if (existingItem == null) {
                    "Сохранить"
                } else {
                    "Обновить"
                }
            )
        }
    }
}

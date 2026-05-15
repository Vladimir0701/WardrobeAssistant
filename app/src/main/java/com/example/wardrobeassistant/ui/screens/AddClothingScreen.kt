package com.example.wardrobeassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothingScreen(
    onSaveClick: (
        String,
        Category,
        ColorGroup,
        Season
    ) -> Unit
) {

    // название одежды
    var clothingName by remember {
        mutableStateOf("")
    }

    // выбранная категория
    var selectedCategory by remember {
        mutableStateOf(Category.BASE_TOP)
    }

    // выбранный цвет
    var selectedColor by remember {
        mutableStateOf(ColorGroup.BLACK)
    }

    // выбранный сезон
    var selectedSeason by remember {
        mutableStateOf(Season.SUMMER)
    }

    // состояния dropdown меню
    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var colorExpanded by remember {
        mutableStateOf(false)
    }

    var seasonExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Добавление одежды",
            style = MaterialTheme.typography.headlineSmall
        )

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
                value = selectedCategory.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
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
                            Text(category.name)
                        },
                        onClick = {

                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // выбор цвета
        ExposedDropdownMenuBox(
            expanded = colorExpanded,
            onExpandedChange = {
                colorExpanded = !colorExpanded
            }
        ) {

            OutlinedTextField(
                value = selectedColor.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text("Цвет")
                }
            )

            ExposedDropdownMenu(
                expanded = colorExpanded,
                onDismissRequest = {
                    colorExpanded = false
                }
            ) {

                ColorGroup.entries.forEach { color ->

                    DropdownMenuItem(
                        text = {
                            Text(color.name)
                        },
                        onClick = {

                            selectedColor = color
                            colorExpanded = false
                        }
                    )
                }
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
                value = selectedSeason.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
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
                            Text(season.name)
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
        Button(
            onClick = {

                onSaveClick(
                    clothingName,
                    selectedCategory,
                    selectedColor,
                    selectedSeason
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Сохранить")
        }
    }
}
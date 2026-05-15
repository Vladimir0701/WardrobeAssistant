package com.example.wardrobeassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.utils.toImageModel

@Composable
fun WardrobeScreen(
    clothingItems: List<ClothingItem>,
    onEditClick: (ClothingItem) -> Unit,
    onDeleteClick: (ClothingItem) -> Unit
) {

    // если список пустой
    // выводим текст что гардероб пока пуст
    if (clothingItems.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            // текст будет примерно по центру
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Гардероб пуст",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        return
    }

    // lazyColumn это список который нормально работает
    // даже с большим количеством элементов
    LazyColumn(
        modifier = Modifier.fillMaxSize(),

        // внутренние отступы
        contentPadding = PaddingValues(16.dp),

        // расстояние между карточками
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // проходимся по всем вещам
        // key чтобы compose правильно перерисовывал
        // после удаления одной из карточек
        items(
            items = clothingItems,
            key = { item -> item.id }
        ) { item ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    // картинка одежды если есть
                    // если нет - показываем плейсхолдер с текстом
                    if (item.imageUri != null) {

                        AsyncImage(
                            model = toImageModel(item.imageUri),
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        // плейсхолдер для вещей без фото
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Без фото",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // название вещи
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // категория
                    Text(
                        text = "Категория: ${item.category}"
                    )

                    // цвет
                    Text(
                        text = "Цвет: ${item.color}"
                    )

                    // сезон
                    Text(
                        text = "Сезон: ${item.season}"
                    )

                    // небольшой отступ перед кнопками
                    Spacer(modifier = Modifier.height(8.dp))

                    // ряд из двух кнопок: изменить и удалить
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        // кнопка редактирования
                        OutlinedButton(
                            onClick = {
                                onEditClick(item)
                            }
                        ) {

                            Text("Изменить")
                        }

                        // кнопка удаления
                        OutlinedButton(
                            onClick = {
                                onDeleteClick(item)
                            }
                        ) {

                            Text("Удалить")
                        }
                    }
                }
            }
        }
    }
}

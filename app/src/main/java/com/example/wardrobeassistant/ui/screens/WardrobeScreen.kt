package com.example.wardrobeassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wardrobeassistant.data.model.ClothingItem

@Composable
fun WardrobeScreen(
    clothingItems: List<ClothingItem>
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
        items(clothingItems) { item ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

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
                }
            }
        }
    }
}
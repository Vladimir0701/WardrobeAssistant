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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.Outfit
import com.example.wardrobeassistant.utils.generateOutfits
import com.example.wardrobeassistant.utils.toImageModel

@Composable
fun OutfitsScreen(
    clothingItems: List<ClothingItem>
) {

    // генерируем все возможные комплекты
    val allOutfits = generateOutfits(clothingItems)

    // если ничего не сгенерировалось - подсказка
    if (allOutfits.isEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Не получилось собрать комплект",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Добавьте хотя бы одну вещь категории " +
                    "\"Базовый верх\" и одну категории \"Низ\"",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        return
    }

    // выбранный лимит вывода
    // null означает показывать все
    var selectedLimit: Int? by remember {
        mutableStateOf(30)
    }

    // доступные варианты для выбора
    // null это "все"
    val limitOptions = listOf<Int?>(10, 30, 100, null)

    // применяем выбранный лимит
    // локальная val чтобы умный каст сработал
    val limit = selectedLimit
    val outfitsToShow = if (limit != null) {
        allOutfits.take(limit)
    } else {
        allOutfits
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // строка с фильтром по количеству
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Показать:",
                style = MaterialTheme.typography.bodyMedium
            )

            limitOptions.forEach { option ->

                FilterChip(
                    selected = option == selectedLimit,
                    onClick = {
                        selectedLimit = option
                    },
                    label = {
                        Text(
                            // null = "Все", иначе число
                            text = option?.toString() ?: "Все"
                        )
                    }
                )
            }
        }

        // дальше список комплектов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // заголовок со счетчиком в списке
            item {

                val totalCount = allOutfits.size
                val shownCount = outfitsToShow.size

                val header = if (totalCount > shownCount) {
                    "Найдено $totalCount комплектов, " +
                        "показано $shownCount"
                } else {
                    "Найдено $totalCount комплектов"
                }

                Text(
                    text = header,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            items(outfitsToShow) { outfit ->
                OutfitCard(outfit = outfit)
            }
        }
    }
}

// карточка с одним сгенерированным комплектом
@Composable
private fun OutfitCard(outfit: Outfit) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // оценка и текстовая метка
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Оценка: ${(outfit.score * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = scoreLabel(outfit.score),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // фото вещей в ряд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                outfit.items.forEach { item ->

                    Column(
                        modifier = Modifier.width(96.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (item.imageUri != null) {

                            AsyncImage(
                                model = toImageModel(item.imageUri),
                                contentDescription = item.name,
                                modifier = Modifier.size(96.dp),
                                contentScale = ContentScale.Crop
                            )

                        } else {

                            Box(
                                modifier = Modifier.size(96.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Без фото",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.category.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// текстовая метка по оценке
private fun scoreLabel(score: Double): String {
    return when {
        score >= 0.85 -> "Отличный"
        score >= 0.70 -> "Хороший"
        score >= 0.50 -> "Средний"
        else -> "Слабый"
    }
}

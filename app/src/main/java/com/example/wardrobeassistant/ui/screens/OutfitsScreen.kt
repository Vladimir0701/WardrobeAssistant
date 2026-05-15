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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

// сколько комплектов максимум показывать в списке
// чтобы не заваливать пользователя
private const val MAX_OUTFITS_TO_SHOW = 30

@Composable
fun OutfitsScreen(
    clothingItems: List<ClothingItem>
) {

    // генерируем комплекты на лету
    // gardrobe items не должен быть огромным, перебор быстрый
    val allOutfits = generateOutfits(clothingItems)

    // показываем топ N лучших комплектов
    val topOutfits = allOutfits.take(MAX_OUTFITS_TO_SHOW)

    // если ничего не сгенерировалось - подсказка
    if (topOutfits.isEmpty()) {

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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // строчка-заголовок со счетчиком
        item {

            val totalCount = allOutfits.size
            val shownCount = topOutfits.size

            // если показываем не все - покажем сколько всего
            val header = if (totalCount > shownCount) {
                "Найдено $totalCount комплектов, " +
                    "показаны топ $shownCount"
            } else {
                "Найдено $totalCount комплектов"
            }

            Text(
                text = header,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        items(topOutfits) { outfit ->
            OutfitCard(outfit = outfit)
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

                        // фото
                        if (item.imageUri != null) {

                            AsyncImage(
                                model = toImageModel(item.imageUri),
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(96.dp),
                                contentScale = ContentScale.Crop
                            )

                        } else {

                            // плейсхолдер если фото нет
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

                        // название категории мелким шрифтом
                        Text(
                            text = item.category.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )

                        // название вещи
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
// чтоб человеку было понятнее чем просто проценты
private fun scoreLabel(score: Double): String {
    return when {
        score >= 0.85 -> "Отличный"
        score >= 0.70 -> "Хороший"
        score >= 0.50 -> "Средний"
        else -> "Слабый"
    }
}

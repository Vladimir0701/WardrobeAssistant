package com.example.wardrobeassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.Season
import com.example.wardrobeassistant.utils.findBestMatch
import com.example.wardrobeassistant.utils.outfitScore
import com.example.wardrobeassistant.utils.toImageModel

@Composable
fun OutfitsScreen(
    clothingItems: List<ClothingItem>
) {

    // три слота комплекта
    // null = пусто
    var slotBase: ClothingItem? by remember {
        mutableStateOf(null)
    }

    var slotBottom: ClothingItem? by remember {
        mutableStateOf(null)
    }

    var slotOuter: ClothingItem? by remember {
        mutableStateOf(null)
    }

    // фильтр сезона
    // null = показывать все
    var seasonFilter: Season? by remember {
        mutableStateOf(null)
    }

    // какой слот сейчас открыл пикер
    // null = диалог закрыт
    var pickerCategory: Category? by remember {
        mutableStateOf(null)
    }

    // пул вещей с учетом фильтра сезона
    // демисезонные попадают в любой фильтр - они универсальные
    val availableItems = if (seasonFilter == null) {
        clothingItems
    } else {
        clothingItems.filter { item ->
            item.season == seasonFilter ||
                item.season == Season.DEMI_SEASON
        }
    }

    // оценка считается по тем слотам что заполнены
    val filled = listOfNotNull(slotBase, slotBottom, slotOuter)
    val score: Double? = if (filled.size >= 2) {
        outfitScore(filled)
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // фильтр сезона
        SeasonFilterRow(
            selected = seasonFilter,
            onChange = { seasonFilter = it }
        )

        // карточка с оценкой
        ScoreCard(score = score)

        // слот базового верха
        SlotCard(
            title = "Базовый верх",
            isOptional = false,
            item = slotBase,
            onPickManual = {
                pickerCategory = Category.BASE_TOP
            },
            onPickAuto = {
                // исключаем текущую вещь чтобы алгоритм выбрал другую
                // если слот пустой - id?. = null и filter ничего не исключит
                val pool = availableItems.filter {
                    it.category == Category.BASE_TOP &&
                        it.id != slotBase?.id
                }
                val newPick = findBestMatch(
                    pool = pool,
                    filledItems = listOfNotNull(
                        slotBottom,
                        slotOuter
                    )
                )
                // если кандидатов нет - оставляем как было
                if (newPick != null) {
                    slotBase = newPick
                }
            },
            onClear = { slotBase = null }
        )

        // слот низа
        SlotCard(
            title = "Низ",
            isOptional = false,
            item = slotBottom,
            onPickManual = {
                pickerCategory = Category.BOTTOM
            },
            onPickAuto = {
                val pool = availableItems.filter {
                    it.category == Category.BOTTOM &&
                        it.id != slotBottom?.id
                }
                val newPick = findBestMatch(
                    pool = pool,
                    filledItems = listOfNotNull(
                        slotBase,
                        slotOuter
                    )
                )
                if (newPick != null) {
                    slotBottom = newPick
                }
            },
            onClear = { slotBottom = null }
        )

        // слот верхнего слоя - необязательный
        SlotCard(
            title = "Верхний слой",
            isOptional = true,
            item = slotOuter,
            onPickManual = {
                pickerCategory = Category.OUTER_LAYER
            },
            onPickAuto = {
                val pool = availableItems.filter {
                    it.category == Category.OUTER_LAYER &&
                        it.id != slotOuter?.id
                }
                val newPick = findBestMatch(
                    pool = pool,
                    filledItems = listOfNotNull(
                        slotBase,
                        slotBottom
                    )
                )
                if (newPick != null) {
                    slotOuter = newPick
                }
            },
            onClear = { slotOuter = null }
        )

        // кнопка которая заполнит пустые слоты разом
        Button(
            onClick = {

                // считаем новые значения в локальных val
                // чтобы каждое следующее findBestMatch видело свежие данные
                val newBase = slotBase ?: findBestMatch(
                    pool = availableItems.filter {
                        it.category == Category.BASE_TOP
                    },
                    filledItems = listOfNotNull(
                        slotBottom,
                        slotOuter
                    )
                )

                val newBottom = slotBottom ?: findBestMatch(
                    pool = availableItems.filter {
                        it.category == Category.BOTTOM
                    },
                    filledItems = listOfNotNull(
                        newBase,
                        slotOuter
                    )
                )

                val newOuter = slotOuter ?: findBestMatch(
                    pool = availableItems.filter {
                        it.category == Category.OUTER_LAYER
                    },
                    filledItems = listOfNotNull(
                        newBase,
                        newBottom
                    )
                )

                slotBase = newBase
                slotBottom = newBottom
                slotOuter = newOuter
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Подобрать всё пустое")
        }
    }

    // если открыт пикер - показываем диалог
    val currentPickerCategory = pickerCategory
    if (currentPickerCategory != null) {

        ClothingPickerDialog(
            title = "Выберите: ${currentPickerCategory.displayName}",
            items = availableItems.filter {
                it.category == currentPickerCategory
            },
            onPick = { picked ->

                // кладем выбранное в соответствующий слот
                when (currentPickerCategory) {
                    Category.BASE_TOP -> slotBase = picked
                    Category.BOTTOM -> slotBottom = picked
                    Category.OUTER_LAYER -> slotOuter = picked
                }
                pickerCategory = null
            },
            onDismiss = { pickerCategory = null }
        )
    }
}

// ряд чипов для фильтра сезона
@Composable
private fun SeasonFilterRow(
    selected: Season?,
    onChange: (Season?) -> Unit
) {

    // null = все, дальше конкретные сезоны
    val options: List<Season?> = listOf(
        null,
        Season.SUMMER,
        Season.WINTER,
        Season.DEMI_SEASON
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Сезон:",
            style = MaterialTheme.typography.bodyMedium
        )

        options.forEach { option ->

            FilterChip(
                selected = option == selected,
                onClick = { onChange(option) },
                label = {
                    Text(
                        text = option?.displayName ?: "Все"
                    )
                }
            )
        }
    }
}

// карточка с оценкой комплекта
@Composable
private fun ScoreCard(score: Double?) {

    Card(modifier = Modifier.fillMaxWidth()) {

        Column(modifier = Modifier.padding(16.dp)) {

            if (score != null) {

                Text(
                    text = "Оценка: ${(score * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = scoreLabel(score),
                    style = MaterialTheme.typography.bodyMedium
                )

            } else {

                Text(
                    text = "Выберите минимум 2 вещи " +
                        "чтобы увидеть оценку",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// карточка одного слота
// показывает пустое состояние или заполненное
@Composable
private fun SlotCard(
    title: String,
    isOptional: Boolean,
    item: ClothingItem?,
    onPickManual: () -> Unit,
    onPickAuto: () -> Unit,
    onClear: () -> Unit
) {

    Card(modifier = Modifier.fillMaxWidth()) {

        Column(modifier = Modifier.padding(16.dp)) {

            // заголовок с пометкой если слот необязательный
            Text(
                text = if (isOptional) {
                    "$title (необязательно)"
                } else {
                    title
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (item == null) {

                // слот пустой - две кнопки на выбор
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onPickManual,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Выбрать")
                    }

                    Button(
                        onClick = onPickAuto,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Подобрать")
                    }
                }

            } else {

                // слот заполнен - показываем вещь и кнопки управления
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (item.imageUri != null) {

                        AsyncImage(
                            model = toImageModel(item.imageUri),
                            contentDescription = item.name,
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Без фото",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${item.color.displayName}, " +
                                item.season.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // три кнопки в ряд
                // Перебрать просит алгоритм найти другую вещь
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onPickManual,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Заменить")
                    }

                    OutlinedButton(
                        onClick = onPickAuto,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Перебрать")
                    }

                    OutlinedButton(
                        onClick = onClear,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Очистить")
                    }
                }
            }
        }
    }
}

// текстовая метка по оценке для пользователя
private fun scoreLabel(score: Double): String {
    return when {
        score >= 0.85 -> "Отличный"
        score >= 0.70 -> "Хороший"
        score >= 0.50 -> "Средний"
        else -> "Слабый"
    }
}

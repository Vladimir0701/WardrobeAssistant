package com.example.wardrobeassistant.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.wardrobeassistant.utils.toImageModel

@Composable
fun WardrobeScreen(
    clothingItems: List<ClothingItem>,
    onEditClick: (ClothingItem) -> Unit,
    onDeleteClick: (ClothingItem) -> Unit
) {

    // если совсем пусто - сразу сообщение
    if (clothingItems.isEmpty()) {

        EmptyWardrobeMessage()
        return
    }

    // фильтр по категории
    // null = показывать все
    var categoryFilter: Category? by remember {
        mutableStateOf(null)
    }

    val filteredItems = if (categoryFilter == null) {
        clothingItems
    } else {
        clothingItems.filter { it.category == categoryFilter }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // строка с чипами фильтра категории
        CategoryFilterRow(
            selected = categoryFilter,
            onChange = { categoryFilter = it }
        )

        // если в выбранной категории ничего нет
        // показываем подсказку вместо пустой сетки
        if (filteredItems.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "В этой категории пока ничего нет",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        } else {

            // сетка из карточек вещей
            // 2 столбца чтобы смотрелось как полки в шкафу
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 4.dp,
                    bottom = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = filteredItems,
                    key = { it.id }
                ) { item ->

                    WardrobeItemCard(
                        item = item,
                        onEdit = { onEditClick(item) },
                        onDelete = { onDeleteClick(item) }
                    )
                }
            }
        }
    }
}

// сообщение когда вообще нет ни одной вещи
@Composable
private fun EmptyWardrobeMessage() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Гардероб пуст",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

// строка чипов фильтра по категории
@Composable
private fun CategoryFilterRow(
    selected: Category?,
    onChange: (Category?) -> Unit
) {

    // null = "Все", потом все категории
    val options: List<Category?> = listOf(null) + Category.entries

    // горизонтальный скролл на случай если чипы не помещаются
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

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

// карточка одной вещи в сетке
@Composable
private fun WardrobeItemCard(
    item: ClothingItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(modifier = Modifier.fillMaxWidth()) {

        Column {

            // картинка занимает квадрат во всю ширину карточки
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {

                if (item.imageUri != null) {

                    AsyncImage(
                        model = toImageModel(item.imageUri),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Text(
                        text = "Без фото",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // текстовая часть
            Column(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                )
            ) {

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = item.color.displayName,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = item.season.displayName,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                // кнопки управления в ряд
                // TextButton чтобы не занимали много места
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    TextButton(
                        onClick = onEdit,
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text(
                            text = "Изменить",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    TextButton(
                        onClick = onDelete,
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 0.dp
                        )
                    ) {
                        Text(
                            text = "Удалить",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

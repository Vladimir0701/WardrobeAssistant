package com.example.wardrobeassistant.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.utils.toImageModel

// диалог выбора одной вещи из списка
// используется и при ручном выборе и при замене
@Composable
fun ClothingPickerDialog(
    title: String,
    items: List<ClothingItem>,
    onPick: (ClothingItem) -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (items.isEmpty()) {

                    // в этой категории и с текущим фильтром сезона ничего нет
                    Text(
                        text = "Нет подходящих вещей. " +
                            "Попробуйте сменить фильтр сезона",
                        style = MaterialTheme.typography.bodyMedium
                    )

                } else {

                    // список вещей
                    // высоту ограничиваем чтобы диалог не вылез за экран
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        items(
                            items = items,
                            key = { it.id }
                        ) { item ->

                            PickerRow(
                                item = item,
                                onClick = {
                                    onPick(item)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Отмена")
                }
            }
        }
    }
}

// строка в списке выбора
@Composable
private fun PickerRow(
    item: ClothingItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // миниатюра фото или плейсхолдер
        if (item.imageUri != null) {

            AsyncImage(
                model = toImageModel(item.imageUri),
                contentDescription = item.name,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Crop
            )

        } else {

            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "—",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "${item.color.displayName}, " +
                    "${item.season.displayName}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

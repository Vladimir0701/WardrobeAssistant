package com.example.wardrobeassistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wardrobeassistant.data.local.AppDatabase
import com.example.wardrobeassistant.data.model.Category
import com.example.wardrobeassistant.data.model.ClothingItem
import com.example.wardrobeassistant.data.model.ColorGroup
import com.example.wardrobeassistant.data.model.Season
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// AndroidViewModel чтобы можно было получить Context
// он нужен для базы данных
class WardrobeViewModel(
    application: Application
) : AndroidViewModel(application) {

    // получаем DAO из синглтона базы данных
    private val dao = AppDatabase
        .getInstance(application)
        .clothingDao()

    // список одежды теперь приходит из БД как поток
    // stateIn превращает обычный Flow в StateFlow
    // StateFlow всегда имеет текущее значение и compose его сразу видит
    val clothingItems: StateFlow<List<ClothingItem>> = dao
        .getAll()
        .stateIn(
            scope = viewModelScope,
            // 5 секунд держим подписку после ухода с экрана
            // на случай если юзер быстро вернется
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addClothingItem(
        name: String,
        category: Category,
        color: ColorGroup,
        season: Season,
        imageUri: String?
    ) {

        // защита от пустого названия
        if (name.isBlank()) {
            return
        }

        // запускаем корутину в скоупе viewmodel
        // корутина живет пока живет viewmodel
        viewModelScope.launch {

            // id=0 - Room сам присвоит автоинкрементом
            dao.insert(
                ClothingItem(
                    name = name.trim(),
                    category = category,
                    color = color,
                    season = season,
                    imageUri = imageUri
                )
            )
        }
    }

    fun updateClothingItem(
        id: Int,
        name: String,
        category: Category,
        color: ColorGroup,
        season: Season,
        imageUri: String?
    ) {

        if (name.isBlank()) {
            return
        }

        viewModelScope.launch {

            // обновляем по id
            dao.update(
                ClothingItem(
                    id = id,
                    name = name.trim(),
                    category = category,
                    color = color,
                    season = season,
                    imageUri = imageUri
                )
            )
        }
    }

    fun removeClothingItem(id: Int) {

        viewModelScope.launch {
            dao.deleteById(id)
        }
    }
}

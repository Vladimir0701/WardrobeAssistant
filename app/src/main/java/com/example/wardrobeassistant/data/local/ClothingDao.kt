package com.example.wardrobeassistant.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.wardrobeassistant.data.model.ClothingItem
import kotlinx.coroutines.flow.Flow

// DAO - Data Access Object
// тут описаны все запросы к таблице вещей
// сами тела методов сгенерирует Room через ksp
@Dao
interface ClothingDao {

    // получить все вещи
    // Flow это поток данных
    // compose будет автоматически обновляться при изменении бд
    @Query("SELECT * FROM clothing_items ORDER BY id DESC")
    fun getAll(): Flow<List<ClothingItem>>

    // добавить вещь
    // suspend значит вызывать из корутины
    // возвращает id который Room присвоил новой вещи
    @Insert
    suspend fun insert(item: ClothingItem): Long

    // обновить существующую
    // Room сам найдет вещь по primary key
    @Update
    suspend fun update(item: ClothingItem)

    // удалить вещь по id
    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}

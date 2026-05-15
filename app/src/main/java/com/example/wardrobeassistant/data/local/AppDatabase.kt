package com.example.wardrobeassistant.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wardrobeassistant.data.model.ClothingItem

// главный класс базы данных
// entities - список таблиц
// version - версия схемы, увеличивать при изменении полей
@Database(
    entities = [ClothingItem::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Room сам реализует этот метод
    abstract fun clothingDao(): ClothingDao

    companion object {

        // Volatile чтобы все потоки видели актуальное значение
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // получаем единственный экземпляр базы
        // synchronized чтобы не создать две базы из двух потоков сразу
        fun getInstance(context: Context): AppDatabase {

            // если уже есть - возвращаем
            val existing = INSTANCE
            if (existing != null) {
                return existing
            }

            return synchronized(this) {

                // повторная проверка внутри блока
                // другой поток мог уже создать пока мы ждали
                val checked = INSTANCE
                if (checked != null) {
                    checked
                } else {

                    val newInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "wardrobe.db"
                    ).build()

                    INSTANCE = newInstance
                    newInstance
                }
            }
        }
    }
}

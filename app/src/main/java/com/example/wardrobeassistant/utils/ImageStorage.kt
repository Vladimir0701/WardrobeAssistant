package com.example.wardrobeassistant.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// копируем выбранное фото в filesDir приложения
// чтобы оно не потерялось после закрытия приложения
// PhotoPicker дает только временный доступ к URI
fun saveImageToInternalStorage(
    context: Context,
    sourceUri: Uri
): String? {

    return try {

        // имя файла на основе текущего времени
        // чтобы новые фото не затирали старые
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val outFile = File(context.filesDir, fileName)

        // открываем поток на чтение исходного фото
        // use закроет поток автоматически
        context.contentResolver
            .openInputStream(sourceUri)
            ?.use { input ->

                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

        // возвращаем абсолютный путь к файлу
        outFile.absolutePath

    } catch (e: Exception) {

        // если что то пошло не так
        e.printStackTrace()
        null
    }
}

// сохраняем готовый Bitmap в filesDir как png
// png чтобы не пострадало качество
fun saveBitmapToInternalStorage(
    context: Context,
    bitmap: Bitmap
): String? {

    return try {

        val fileName = "img_${System.currentTimeMillis()}.png"
        val outFile = File(context.filesDir, fileName)

        outFile.outputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }

        outFile.absolutePath

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// весь пайплайн обработки выбранной фотографии
// пробуем убрать фон через ML Kit
// если получилось - сохраняем результат
// если нет - сохраняем оригинал
suspend fun processAndSaveImage(
    context: Context,
    sourceUri: Uri
): String? = withContext(Dispatchers.IO) {

    // пробуем отделить одежду от фона
    val processed = removeBackgroundOrNull(
        context = context,
        sourceUri = sourceUri
    )

    if (processed != null) {

        // получилось - сохраняем картинку с белым фоном
        saveBitmapToInternalStorage(
            context = context,
            bitmap = processed
        )

    } else {

        // не получилось - сохраним хотя бы оригинал
        // чтобы юзер не остался без фото
        saveImageToInternalStorage(
            context = context,
            sourceUri = sourceUri
        )
    }
}

// преобразуем сохраненную строку в модель для Coil
// если это путь к файлу - оборачиваем в File
// если что то другое (старые content URI) - оставляем как есть
fun toImageModel(imageUri: String?): Any? {

    if (imageUri == null) {
        return null
    }

    if (imageUri.startsWith("/")) {
        return File(imageUri)
    }

    return imageUri
}

package com.example.wardrobeassistant.utils

import android.content.Context
import android.net.Uri
import java.io.File

// преобразуем сохраненную строку в модель для Coil
// если это путь к файлу - оборачиваем в File
// если что то другое (старые content URI) - оставляем как есть
fun toImageModel(imageUri: String?): Any? {

    if (imageUri == null) {
        return null
    }

    // пути в Android начинаются со слеша
    if (imageUri.startsWith("/")) {
        return File(imageUri)
    }

    // на случай старых записей с content:// URI
    return imageUri
}


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
        // потом обернем в File когда будем грузить через Coil
        outFile.absolutePath

    } catch (e: Exception) {

        // если что то пошло не так
        // в логи выведем но крашить не будем
        e.printStackTrace()
        null
    }
}

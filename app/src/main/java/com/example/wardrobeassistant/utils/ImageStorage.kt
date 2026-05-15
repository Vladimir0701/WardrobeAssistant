package com.example.wardrobeassistant.utils

import android.content.Context
import android.net.Uri
import java.io.File

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

        // возвращаем file URI чтобы Coil потом загрузил
        Uri.fromFile(outFile).toString()

    } catch (e: Exception) {

        // если что то пошло не так
        // в логи выведем но крашить не будем
        e.printStackTrace()
        null
    }
}

package com.example.wardrobeassistant.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

// убирает фон у фотографии через ML Kit
// возвращает Bitmap где вместо фона - белый цвет
// если что то пошло не так - возвращает null
suspend fun removeBackgroundOrNull(
    context: Context,
    sourceUri: Uri
): Bitmap? = suspendCancellableCoroutine { cont ->

    try {

        // готовим картинку для ml kit
        val input = InputImage.fromFilePath(context, sourceUri)

        // включаем получение foreground bitmap
        // это и есть результат - картинка где фон прозрачный
        val options = SubjectSegmenterOptions.Builder()
            .enableForegroundBitmap()
            .build()

        val segmenter = SubjectSegmentation.getClient(options)

        segmenter.process(input)
            .addOnSuccessListener { result ->

                val foreground = result.foregroundBitmap

                if (foreground == null) {

                    // модель не нашла объект
                    cont.resume(null)

                } else {

                    // делаем белый холст того же размера
                    // и накладываем сверху наш foreground
                    // там где фон был прозрачным - останется белый
                    val white = Bitmap.createBitmap(
                        foreground.width,
                        foreground.height,
                        Bitmap.Config.ARGB_8888
                    )

                    val canvas = Canvas(white)
                    canvas.drawColor(Color.WHITE)
                    canvas.drawBitmap(foreground, 0f, 0f, null)

                    cont.resume(white)
                }
            }
            .addOnFailureListener { e ->

                // например модель еще не скачалась
                e.printStackTrace()
                cont.resume(null)
            }

    } catch (e: Exception) {

        // не смогли прочитать картинку или что то другое
        e.printStackTrace()
        cont.resume(null)
    }
}

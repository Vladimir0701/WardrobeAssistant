package com.example.wardrobeassistant.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.moduleinstall.InstallStatusListener
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.common.moduleinstall.ModuleInstallStatusUpdate
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions

private const val TAG = "ModelInstaller"

// явно запрашиваем загрузку модели сегментации через play services
// без этого модель просто ждет когда ее кто то запросит
// и наш ML код падает с ошибкой "module not downloaded"
fun requestSubjectSegmentationModel(context: Context) {

    val segmenter = SubjectSegmentation.getClient(
        SubjectSegmenterOptions.Builder()
            .enableForegroundBitmap()
            .build()
    )

    val moduleInstall = ModuleInstall.getClient(context)

    // слушатель прогресса
    // нужен чтобы понять что модель устанавливается
    val listener = InstallStatusListener { update ->

        when (update.installState) {

            ModuleInstallStatusUpdate.InstallState.STATE_DOWNLOADING -> {
                val total = update.progressInfo?.totalBytesToDownload ?: 0
                val done = update.progressInfo?.bytesDownloaded ?: 0
                Log.d(TAG, "скачиваем модель: $done из $total байт")
            }

            ModuleInstallStatusUpdate.InstallState.STATE_INSTALLING -> {
                Log.d(TAG, "модель устанавливается")
            }

            ModuleInstallStatusUpdate.InstallState.STATE_COMPLETED -> {
                Log.d(TAG, "модель установлена")
                Toast.makeText(
                    context,
                    "Модель обработки фото готова",
                    Toast.LENGTH_SHORT
                ).show()
            }

            ModuleInstallStatusUpdate.InstallState.STATE_FAILED -> {
                Log.e(TAG, "установка модели упала")
            }

            else -> {
                Log.d(TAG, "состояние установки: ${update.installState}")
            }
        }
    }

    val request = ModuleInstallRequest.newBuilder()
        .addApi(segmenter)
        .setListener(listener)
        .build()

    moduleInstall.installModules(request)
        .addOnSuccessListener { response ->

            if (response.areModulesAlreadyInstalled()) {
                Log.d(TAG, "модель уже была установлена")
            } else {
                Log.d(TAG, "запросили установку, ждем")
                Toast.makeText(
                    context,
                    "Загружается модель обработки фото...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        .addOnFailureListener { e ->
            Log.e(TAG, "не удалось даже запросить установку", e)
        }
}

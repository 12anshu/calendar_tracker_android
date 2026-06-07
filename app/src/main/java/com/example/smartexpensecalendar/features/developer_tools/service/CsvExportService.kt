package com.example.smartexpensecalendar.features.developer_tools.service

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvExportService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun exportToCsv(data: List<Map<String, String>>, fileNamePrefix: String): String = withContext(Dispatchers.IO) {
        if (data.isEmpty()) return@withContext "No data to export"
        
        val headers = data.first().keys.joinToString(",")
        val csvData = StringBuilder("$headers\n")
        
        data.forEach { row ->
            val line = row.values.joinToString(",") { value ->
                "\"${value.replace("\"", "\"\"")}\""
            }
            csvData.append("$line\n")
        }

        val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.csv"
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(csvData.toString().toByteArray())
                    }
                    return@withContext "File saved to Downloads: $fileName"
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)
                file.writeText(csvData.toString())
                return@withContext "File saved to Downloads: ${file.absolutePath}"
            }
        } catch (e: Exception) {
            return@withContext "Export failed: ${e.message}"
        }
        
        return@withContext "Export failed"
    }
}

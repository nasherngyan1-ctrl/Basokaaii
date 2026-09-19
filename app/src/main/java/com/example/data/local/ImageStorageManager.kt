package com.example.data.local

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageStorageManager {

  private const val DIRECTORY_NAME = "generated_images"

  /**
   * Saves image bytes into private internal storage and returns the local file URI string.
   */
  suspend fun saveInternalImage(
    context: Context,
    imageBytes: ByteArray,
    prefix: String = "basoka_gen"
  ): String = withContext(Dispatchers.IO) {
    val dir = File(context.filesDir, DIRECTORY_NAME).apply {
      if (!exists()) mkdirs()
    }
    val fileName = "${prefix}_${System.currentTimeMillis()}.png"
    val file = File(dir, fileName)
    FileOutputStream(file).use { out ->
      out.write(imageBytes)
      out.flush()
    }
    Uri.fromFile(file).toString()
  }

  /**
   * Exports the generated image to device gallery (MediaStore) so it is visible
   * in Google Photos, Gallery, and system media pickers.
   */
  suspend fun saveToDeviceGallery(
    context: Context,
    localUriString: String,
    title: String = "Basoka AI Generated Image"
  ): Result<Uri> = withContext(Dispatchers.IO) {
    try {
      val localFile = resolveLocalFile(context, localUriString)
        ?: return@withContext Result.failure(IllegalStateException("Local image file not found"))

      val fileName = "Basoka_${System.currentTimeMillis()}.png"
      val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.TITLE, title)
        put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BasokaAI")
          put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
      }

      val resolver = context.contentResolver
      val targetUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ?: return@withContext Result.failure(IllegalStateException("Failed to insert into MediaStore"))

      resolver.openOutputStream(targetUri)?.use { outStream ->
        localFile.inputStream().use { inStream ->
          inStream.copyTo(outStream)
        }
      } ?: return@withContext Result.failure(IllegalStateException("Failed to write to MediaStore output stream"))

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        resolver.update(targetUri, contentValues, null, null)
      }

      Result.success(targetUri)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Generates a shareable URI for this image using FileProvider
   */
  fun getShareableUri(context: Context, localUriString: String): Uri? {
    return try {
      val file = resolveLocalFile(context, localUriString) ?: return null
      val authority = "${context.packageName}.fileprovider"
      FileProvider.getUriForFile(context, authority, file)
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Resolves a local URI string to a File
   */
  fun resolveLocalFile(context: Context, uriString: String): File? {
    return try {
      val uri = Uri.parse(uriString)
      when (uri.scheme) {
        "file" -> File(uri.path ?: return null)
        null -> File(uriString)
        else -> {
          // If content or direct path
          if (uri.path != null) File(uri.path!!) else null
        }
      }
    } catch (e: Exception) {
      null
    }
  }
}

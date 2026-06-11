package com.example.playermanager.players

import android.net.Uri
import android.widget.ImageView
import com.example.playermanager.R
import java.io.File

object PlayerPhotoLoader {

    fun load(imageView: ImageView, photoUri: String?) {
        if (photoUri.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.placeholder_player)
            return
        }

        val uri = resolveUri(photoUri)
        if (uri == null) {
            imageView.setImageResource(R.drawable.placeholder_player)
            return
        }

        try {
            imageView.setImageURI(null)
            imageView.setImageURI(uri)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.placeholder_player)
        }
    }

    /** Saves image to app storage and returns a stable absolute path string. */
    fun saveToInternalStorage(
        context: android.content.Context,
        sourceUri: Uri
    ): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val fileName = "player_img_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            file.outputStream().use { output -> inputStream.copyTo(output) }
            inputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resolveUri(photoUri: String): Uri? {
        return try {
            when {
                photoUri.startsWith("/") -> {
                    val file = File(photoUri)
                    if (file.exists()) Uri.fromFile(file) else null
                }
                photoUri.startsWith("file:") -> {
                    val file = File(Uri.parse(photoUri).path ?: return null)
                    if (file.exists()) Uri.fromFile(file) else null
                }
                else -> Uri.parse(photoUri)
            }
        } catch (e: Exception) {
            null
        }
    }
}

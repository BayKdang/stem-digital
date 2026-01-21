package com.stemdigital.inventorytracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    /**
     * Save bitmap image to app's internal storage
     * @param context Android context
     * @param bitmap The bitmap to save
     * @param itemId The item ID for unique naming
     * @return The file path where image was saved, or null if failed
     */
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, itemId: Int): String? {
        return try {
            val imagesDir = File(context.filesDir, "item_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdir()
            }

            val fileName = "item_${itemId}_${System.currentTimeMillis()}.jpg"
            val imageFile = File(imagesDir, fileName)

            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat. JPEG, 85, fos)
                fos.flush()
            }

            imageFile.absolutePath
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load bitmap from file path
     * @param imagePath The file path to load from
     * @param maxWidth Maximum width to scale to
     * @param maxHeight Maximum height to scale to
     * @return The loaded bitmap, or null if failed
     */
    fun loadBitmapFromPath(imagePath: String, maxWidth: Int = 500, maxHeight: Int = 500): Bitmap? {
        return try {
            if (imagePath.isEmpty()) return null

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)

            val scale = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inSampleSize = scale
            options.inJustDecodeBounds = false

            BitmapFactory.decodeFile(imagePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Delete image file
     * @param imagePath The file path to delete
     * @return True if deleted successfully
     */
    fun deleteImage(imagePath: String): Boolean {
        return try {
            if (imagePath.isEmpty())return false
            val file = File(imagePath)
            file.exists() && file.delete()
        } catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Calculate the best sample size for image decoding
     */
    private fun calculateInSampleSize(
        options: BitmapFactory. Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height:  Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight:  Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
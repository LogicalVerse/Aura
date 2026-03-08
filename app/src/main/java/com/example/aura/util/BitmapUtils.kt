package com.example.aura.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Utility functions for image processing before sending to Gemini.
 */
object BitmapUtils {

    /** Maximum dimension (width or height) before we downscale for API calls. */
    private const val MAX_DIMENSION = 1024

    /** JPEG quality for compression (0-100). */
    private const val JPEG_QUALITY = 85

    /**
     * Resize a bitmap so its largest dimension is at most [MAX_DIMENSION].
     * Preserves aspect ratio. Returns the original if already within bounds.
     */
    fun resizeForApi(bitmap: Bitmap): Bitmap {
        val maxDim = maxOf(bitmap.width, bitmap.height)
        if (maxDim <= MAX_DIMENSION) return bitmap

        val scale = MAX_DIMENSION.toFloat() / maxDim
        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Compress a bitmap to a JPEG byte array suitable for API transmission.
     */
    fun compressToBytes(bitmap: Bitmap, quality: Int = JPEG_QUALITY): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }

    /**
     * Full pipeline: resize + compress.
     * Use this before sending images to Gemini.
     */
    fun prepareForApi(bitmap: Bitmap): Bitmap {
        return resizeForApi(bitmap)
    }
}

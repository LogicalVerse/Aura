package com.example.aura.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Camera screen.
 *
 * Manages camera state and the captured image.
 * Module A owner: extend this with CameraX integration.
 */
class CameraViewModel : ViewModel() {

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    /** The most recently captured outfit photo. Null until capture. */
    val capturedImage: StateFlow<Bitmap?> = _capturedImage.asStateFlow()

    private val _isCameraReady = MutableStateFlow(false)
    /** True once CameraX preview is bound and ready. */
    val isCameraReady: StateFlow<Boolean> = _isCameraReady.asStateFlow()

    private val _permissionGranted = MutableStateFlow(false)
    /** True if CAMERA permission has been granted. */
    val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

    /** Called when camera permission result is received. */
    fun onPermissionResult(granted: Boolean) {
        _permissionGranted.value = granted
    }

    /** Called when CameraX preview is successfully bound. */
    fun onCameraReady() {
        _isCameraReady.value = true
    }

    /** Called when user captures a photo. Stores the bitmap. */
    fun onImageCaptured(bitmap: Bitmap) {
        _capturedImage.value = bitmap
    }

    /** Reset to retake a photo. */
    fun retake() {
        _capturedImage.value = null
    }
}

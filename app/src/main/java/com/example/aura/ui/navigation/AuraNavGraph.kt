package com.example.aura.ui.navigation

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aura.data.repository.AuraRepository
import com.example.aura.ui.analysis.AnalysisScreen
import com.example.aura.ui.analysis.AnalysisViewModel
import com.example.aura.ui.camera.CameraScreen
import com.example.aura.ui.camera.CameraViewModel
import com.example.aura.ui.chat.ChatScreen
import com.example.aura.ui.chat.ChatViewModel

/**
 * Navigation routes for the Aura app.
 */
object AuraRoutes {
    const val CAMERA = "camera"
    const val ANALYSIS = "analysis"
    const val CHAT = "chat"
}

/**
 * Main navigation graph for the Aura app.
 *
 * Flow: Camera → Analysis → Chat
 *
 * Module E (Integration) owner: This is the wiring layer.
 * Connects screens to shared state through ViewModels.
 *
 * @param repository Shared AuraRepository instance
 * @param navController Navigation controller (default creates one)
 */
@Composable
fun AuraNavGraph(
    repository: AuraRepository,
    navController: NavHostController = rememberNavController()
) {
    // Shared state: the captured outfit image
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    // ViewModels — in a real app, use Hilt. For hackathon, manual creation.
    val cameraViewModel = remember { CameraViewModel() }
    val analysisViewModel = remember { AnalysisViewModel(repository) }
    val chatViewModel = remember { ChatViewModel(repository) }

    NavHost(
        navController = navController,
        startDestination = AuraRoutes.CAMERA
    ) {
        // ── Camera Screen ────────────────────────────────
        composable(AuraRoutes.CAMERA) {
            CameraScreen(
                viewModel = cameraViewModel,
                onImageCaptured = { bitmap ->
                    capturedImage = bitmap
                    navController.navigate(AuraRoutes.ANALYSIS)
                }
            )
        }

        // ── Analysis Screen ──────────────────────────────
        composable(AuraRoutes.ANALYSIS) {
            capturedImage?.let { image ->
                AnalysisScreen(
                    outfitImage = image,
                    viewModel = analysisViewModel,
                    onStartChat = {
                        navController.navigate(AuraRoutes.CHAT)
                    },
                    onRetake = {
                        repository.clearSession()
                        cameraViewModel.retake()
                        navController.popBackStack(AuraRoutes.CAMERA, inclusive = false)
                    }
                )
            }
        }

        // ── Chat Screen ──────────────────────────────────
        composable(AuraRoutes.CHAT) {
            ChatScreen(
                viewModel = chatViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

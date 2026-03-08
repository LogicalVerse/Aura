package com.example.aura.ui.analysis

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.ui.components.LoadingAnimation
import com.example.aura.ui.components.OutfitTagChip

/**
 * Screen that displays the outfit analysis results.
 *
 * Shows: captured image, detected items as chips, style label,
 * and a button to start the stylist chat.
 *
 * @param outfitImage The captured outfit photo
 * @param viewModel AnalysisViewModel instance
 * @param onStartChat Called when user taps "Start Styling"
 * @param onRetake Called when user wants to recapture
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalysisScreen(
    outfitImage: Bitmap,
    viewModel: AnalysisViewModel,
    onStartChat: () -> Unit,
    onRetake: () -> Unit
) {
    val analysis by viewModel.outfitAnalysis.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Trigger analysis on first composition
    LaunchedEffect(outfitImage) {
        viewModel.analyzeOutfit(outfitImage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outfit Image
        Image(
            bitmap = outfitImage.asImageBitmap(),
            contentDescription = "Your outfit",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Loading State
        if (isLoading) {
            LoadingAnimation(message = "Analyzing your outfit...")
        }

        // Error State
        error?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Analysis Results
        analysis?.let { result ->
            AnalysisResults(analysis = result)

            Spacer(modifier = Modifier.height(24.dp))

            // Start Styling Button
            Button(
                onClick = onStartChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "✨ Start Styling",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Retake Button
        OutlinedButton(
            onClick = onRetake,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retake Photo")
        }
    }
}

/**
 * Displays the analysis results: style label and detected item chips.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnalysisResults(analysis: OutfitAnalysis) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically()
    ) {
        Column {
            // Style Label
            Text(
                text = analysis.overallStyle.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Summary
            Text(
                text = analysis.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detected Items as Chips
            Text(
                text = "Detected Items",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                analysis.items.forEach { item ->
                    OutfitTagChip(
                        label = item.name,
                        color = item.color
                    )
                }
            }
        }
    }
}

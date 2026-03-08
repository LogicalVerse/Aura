package com.example.aura.ui.analysis

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aura.data.model.OutfitAnalysis
import com.example.aura.data.repository.AuraRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Analysis screen.
 *
 * Triggers outfit analysis and exposes results for display.
 */
class AnalysisViewModel(
    private val repository: AuraRepository
) : ViewModel() {

    /** Outfit analysis result — null while analyzing. */
    val outfitAnalysis: StateFlow<OutfitAnalysis?> = repository.outfitAnalysis

    /** True while Gemini is analyzing the outfit image. */
    val isLoading: StateFlow<Boolean> = repository.isLoading

    /** Error message if analysis failed. */
    val error: StateFlow<String?> = repository.error

    /**
     * Start analyzing the captured outfit image.
     * Results will flow through [outfitAnalysis].
     */
    fun analyzeOutfit(image: Bitmap) {
        viewModelScope.launch {
            repository.analyzeOutfit(image)
        }
    }
}

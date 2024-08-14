package com.example.transpose.ui.common

sealed class UiState {
    data object Initial : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val message: String) : UiState()
}
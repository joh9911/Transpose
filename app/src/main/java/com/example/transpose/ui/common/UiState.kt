package com.example.transpose.ui.common

sealed class UiState<out T> {
    data object Initial: UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
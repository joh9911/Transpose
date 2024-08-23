package com.example.transpose.utils

sealed class PlayerUiEvent {
    data object PlayPause : PlayerUiEvent()
    data class CurrentAudioChanged(val index: Int) : PlayerUiEvent()
    data class SeekTo(val position: Float) : PlayerUiEvent()
    data class UpdateProgress(val progress: Float) : PlayerUiEvent()
    data object SeekToNext : PlayerUiEvent()
    data object SeekToPrevious : PlayerUiEvent()
    data object Backward : PlayerUiEvent()
    data object Forward : PlayerUiEvent()
}
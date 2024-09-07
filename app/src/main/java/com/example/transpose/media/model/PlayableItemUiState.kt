package com.example.transpose.media.model

sealed class PlayableItemUiState {
    data object Initial : PlayableItemUiState()

    data class BasicInfoLoaded(val basicInfo: PlayableItemBasicInfoData) : PlayableItemUiState()

    data class FullInfoLoaded(val fullInfo: PlayableItemData) : PlayableItemUiState()

    data class Error(val message: String?) : PlayableItemUiState()
}
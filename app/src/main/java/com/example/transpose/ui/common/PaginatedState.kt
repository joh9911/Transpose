package com.example.transpose.ui.common

import com.example.transpose.data.model.newpipe.NewPipeContentListData

sealed class PaginatedState{
    data object Initial : PaginatedState()
    data object Loading : PaginatedState()
    data class Success(
        val items: List<NewPipeContentListData>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
        val loadMoreError: String? = null
    ) : PaginatedState()
    data class Error(val message: String) : PaginatedState()
}
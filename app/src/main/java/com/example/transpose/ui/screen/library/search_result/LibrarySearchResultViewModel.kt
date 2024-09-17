package com.example.transpose.ui.screen.library.search_result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.repository.VideoPager
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.ui.common.PaginatedState
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrarySearchResultViewModel @Inject constructor(
    private val newPipeRepository: NewPipeRepository
): ViewModel() {

    private val _searchResultsState = MutableStateFlow<PaginatedState>(PaginatedState.Initial)
    val searchResultsState = _searchResultsState.asStateFlow()

    private var searchPager: VideoPager? = null

    fun initializeSearchPager(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchResultsState.value = PaginatedState.Loading
        try {
            searchPager = newPipeRepository.createSearchPager(query)
            firstFetchSearchResult()
        } catch (e: Exception) {
            _searchResultsState.value = PaginatedState.Error(e.toString())
        }
    }

    private fun firstFetchSearchResult() = viewModelScope.launch(Dispatchers.IO) {
        searchPager ?: return@launch
        try {
            val searchResults = newPipeRepository.fetchSearchResult(searchPager!!)

            if (searchResults.isSuccess) {
                val items = searchResults.getOrElse { emptyList() }
                _searchResultsState.value = PaginatedState.Success(
                    items = items,
                    hasMore = searchPager!!.isHasNextPage(),
                    isLoadingMore = false
                )
            } else {
                _searchResultsState.value = PaginatedState.Error(searchResults.exceptionOrNull().toString())
            }
        } catch (e: Exception) {
            Logger.e("Error fetching search results", e)
            _searchResultsState.value = PaginatedState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun loadMoreSearchResults() = viewModelScope.launch(Dispatchers.IO) {
        val currentState = _searchResultsState.value
        if (currentState !is PaginatedState.Success || currentState.isLoadingMore) return@launch

        _searchResultsState.value = currentState.copy(isLoadingMore = true)

        try {
            val result = newPipeRepository.fetchSearchResult(searchPager!!)
            if (result.isSuccess) {
                val newItems = result.getOrDefault(emptyList())
                _searchResultsState.value = currentState.copy(
                    items = currentState.items + newItems,
                    hasMore = searchPager!!.isHasNextPage(),
                    isLoadingMore = false
                )
            } else {
                _searchResultsState.value = currentState.copy(
                    isLoadingMore = false,
                    loadMoreError = result.exceptionOrNull()?.toString()
                )
                Logger.d("loadMoreSearchResults ${result.exceptionOrNull()}")
            }
        } catch (e: Exception) {
            _searchResultsState.value = currentState.copy(
                isLoadingMore = false,
                loadMoreError = e.toString()
            )
            Logger.d("loadMoreSearchResults catch ${e}")
        }
    }
}
package com.example.transpose.ui.screen.home.search_result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.newpipe.NewPipeContentListData
import com.example.transpose.data.repository.VideoPager
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.ui.common.UiState
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSearchResultViewModel @Inject constructor(
    private val newPipeRepository: NewPipeRepository
) : ViewModel(){

    // SearchResult Screen
    private val _searchUiState = MutableStateFlow<UiState>(UiState.Initial)
    val searchUiState= _searchUiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NewPipeContentListData>>(emptyList())
    val searchResults= _searchResults.asStateFlow()

    private val _hasMoreSearchItems = MutableStateFlow(false)
    val hasMoreSearchItems = _hasMoreSearchItems.asStateFlow()

    private val _isMoreSearchItemsLoading = MutableStateFlow(false)
    val isMoreItemsLoading = _isMoreSearchItemsLoading.asStateFlow()

    private var searchPager: VideoPager? = null

    fun initializeSearchPager(query: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchUiState.value = UiState.Loading
        try {
            searchPager = newPipeRepository.createSearchPager(query)
            firstFetchSearchResult()
        }catch (e: Exception){
            _searchUiState.value = UiState.Error(e.toString())
        }
    }

    private fun firstFetchSearchResult() = viewModelScope.launch(Dispatchers.IO) {
        searchPager ?: return@launch
        try {
            val searchResults = newPipeRepository.fetchSearchResult(searchPager!!)

            if (searchResults.isSuccess){
                _searchResults.value = searchResults.getOrElse { emptyList() }
                _searchUiState.value = UiState.Success
                _hasMoreSearchItems.value = searchPager!!.isHasNextPage()
            }
            if (searchResults.isFailure){
                _searchResults.value = emptyList()
                _searchUiState.value = UiState.Error(searchResults.exceptionOrNull().toString())
            }
            // 다음 페이지 로직은 주석 처리된 상태로 유지
        } catch (e: Exception) {
            Logger.e("Error fetching search results", e)
            _searchUiState.value = UiState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun loadMoreSearchResults() = viewModelScope.launch(Dispatchers.IO) {
        _isMoreSearchItemsLoading.value = false
        searchPager ?: return@launch

        try {
            val result = newPipeRepository.fetchSearchResult(searchPager!!)
            if (result.isSuccess){
                _searchResults.value += result.getOrDefault(emptyList())
                _isMoreSearchItemsLoading.value = false
                _hasMoreSearchItems.value = searchPager!!.isHasNextPage()
            }
            if (result.isFailure){
                Logger.d("loadMoreSearchResults ${result.exceptionOrNull()}")
                _isMoreSearchItemsLoading.value = false
            }
        }catch (e: Exception){
            _isMoreSearchItemsLoading.value = false
            Logger.d("loadMoreSearchResults catch ${e}")
        }
    }


}
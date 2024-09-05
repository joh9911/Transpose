package com.example.transpose

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.newpipe.NewPipeChannelData
import com.example.transpose.data.model.newpipe.NewPipeVideoData
import com.example.transpose.data.repository.database.MyPlaylistDBRepository
import com.example.transpose.data.repository.database.MyPlaylistDBRepositoryImpl
import com.example.transpose.data.repository.newpipe.NewPipeRepository
import com.example.transpose.data.repository.suggestion_keyword.SuggestionKeywordRepository
import com.example.transpose.ui.components.appbar.SearchWidgetState
import com.example.transpose.utils.Logger
import com.example.transpose.utils.PermissionUtils
import com.example.transpose.utils.SuggestionKeywordStringExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject
@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val suggestionKeywordRepository: SuggestionKeywordRepository,
    private val newPipeRepository: NewPipeRepository,
    private val playlistDBRepository: MyPlaylistDBRepository,
    @ApplicationContext private val context: Context  // Application Context 주입
): ViewModel() {

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        _permissionGranted.value = PermissionUtils.checkPermissions(context)
    }

    fun setPermissionGranted(granted: Boolean) {
        _permissionGranted.value = granted
    }

    fun requestPermissions(launcher: (Array<String>) -> Unit) {
        PermissionUtils.requestPermissions(launcher)
    }


    private val _searchWidgetState = MutableStateFlow(SearchWidgetState.CLOSED)
    val searchWidgetState = _searchWidgetState.asStateFlow()

    private val _searchTextState = MutableStateFlow("")
    val searchTextState = _searchTextState.asStateFlow()

    private val _isSearchBarActive = MutableStateFlow(true)
    val isSearchBarActive = _isSearchBarActive.asStateFlow()

    fun closeSearchBar(){
        _searchWidgetState.value = SearchWidgetState.CLOSED
        updateSearchTextState("")
        clearSuggestionKeywords()
    }

    fun openSearchBar(){
        _searchWidgetState.value = SearchWidgetState.OPENED
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun updateIsSearchBarExpanded(boolean: Boolean){
        _isSearchBarActive.value = boolean
    }


    private val _suggestionKeywords: MutableStateFlow<ArrayList<String>> = MutableStateFlow(
        arrayListOf()
    )
    val suggestionKeywords = _suggestionKeywords.asStateFlow()

    fun clearSuggestionKeywords(){
        _suggestionKeywords.value = arrayListOf()
    }

    fun getSuggestionKeyword(query: String) = viewModelScope.launch{
        val suggestionKeywordStringExtractor = SuggestionKeywordStringExtractor()
        suggestionKeywordRepository.getSuggestionKeywords(query)
            .onSuccess { value: ResponseBody ->
                value.string().let {
                    val responseString = suggestionKeywordStringExtractor.convertStringUnicodeToKorean(it)
                    val splitBracketList = responseString.split('[')
                    val splitCommaList = splitBracketList[2].split(',')
                    if (splitCommaList[0] != "]]" && splitCommaList[0] != '"'.toString()) {
                        _suggestionKeywords.value = suggestionKeywordStringExtractor.addSubstringToSuggestionKeyword(splitCommaList)
                    }
                    Logger.d("${suggestionKeywords.value}")
                }
            }
            .onFailure {
                Logger.d("검색어 실패")
            }

    }


    private val _normalizedOffset = MutableStateFlow(0f)
    val normalizedOffset = _normalizedOffset.asStateFlow()

    fun updateNormalizedOffset(requiredOffset: Float){
        _normalizedOffset.value = requiredOffset
    }

    private val _bottomSheetDraggableArea = MutableStateFlow<Rect?>(null)
    val bottomSheetDraggableArea = _bottomSheetDraggableArea.asStateFlow()

    fun updateBottomSheetDraggableArea(rect: Rect){
        _bottomSheetDraggableArea.value = rect
    }

    private val _isBottomSheetDraggable = MutableStateFlow(true)
    val isBottomSheetDraggable = _isBottomSheetDraggable.asStateFlow()

    fun updateIsBottomSheetDraggable(boolean: Boolean){
        _isBottomSheetDraggable.value = boolean
    }

    private val _bottomSheetState = MutableStateFlow(SheetValue.Hidden)
    val bottomSheetState = _bottomSheetState.asStateFlow()

    fun expandBottomSheet() {
        viewModelScope.launch {
            _bottomSheetState.value = SheetValue.Expanded

        }
    }

    fun partialExpandBottomSheet(){

        viewModelScope.launch {
            _bottomSheetState.value = SheetValue.PartiallyExpanded

        }
    }

    fun hideBottomSheet() {

        viewModelScope.launch {
            _bottomSheetState.value = SheetValue.Hidden

        }
    }


    fun addVideoToPlaylist(video: NewPipeVideoData, playlistId: Long) = viewModelScope.launch(Dispatchers.IO){
        playlistDBRepository.addVideoToPlaylist(video, playlistId)
    }

    private val _channelData = MutableStateFlow<NewPipeChannelData?>(null)
    val channelData = _channelData.asStateFlow()

    fun fetchChannelData(item: NewPipeVideoData) = viewModelScope.launch(Dispatchers.IO){
        try {
            newPipeRepository.fetchChannelDataByChannelUrl(item.uploaderUrl ?: "")
        }catch (e: Exception){

        }
    }


}
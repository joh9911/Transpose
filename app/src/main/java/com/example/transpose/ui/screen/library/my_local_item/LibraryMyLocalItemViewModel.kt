package com.example.transpose.ui.screen.library.my_local_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transpose.data.model.local_file.LocalFileData
import com.example.transpose.data.repository.local_file.LocalFileRepositoryImpl
import com.example.transpose.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryMyLocalItemViewModel @Inject constructor(
    private val localFileRepositoryImpl: LocalFileRepositoryImpl
) : ViewModel() {

    private val _audioFiles = MutableStateFlow<List<LocalFileData>>(emptyList())
    val audioFiles: StateFlow<List<LocalFileData>> = _audioFiles

    private val _videoFiles = MutableStateFlow<List<LocalFileData>>(emptyList())
    val videoFiles: StateFlow<List<LocalFileData>> = _videoFiles

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun loadAudioFiles() = viewModelScope.launch(Dispatchers.IO) {
        localFileRepositoryImpl.getAudioFiles()
            .onSuccess { files ->
                _audioFiles.value = files
            }
            .onFailure { error ->
                _errorMessage.value = "오디오 파일 로딩 실패: ${error.message}"
            }
    }

    fun loadVideoFiles() = viewModelScope.launch(Dispatchers.IO) {
        localFileRepositoryImpl.getVideoFiles()
            .onSuccess { files ->
                Logger.d("${files}")
                _videoFiles.value = files
            }
            .onFailure { error ->
                Logger.d("${error}")
                _errorMessage.value = "비디오 파일 로딩 실패: ${error.message}"
            }

    }

    fun deleteFile(file: LocalFileData) = viewModelScope.launch(Dispatchers.IO) {
        localFileRepositoryImpl.deleteFile(file)
            .onSuccess { isDeleted ->
                if (isDeleted) {
                    // 파일 삭제 성공 처리
                    _audioFiles.value = _audioFiles.value.filter { it.id != file.id }
                    _videoFiles.value = _videoFiles.value.filter { it.id != file.id }
                } else {
                    _errorMessage.value = "파일 삭제 실패"
                }
            }
            .onFailure { error ->
                _errorMessage.value = "파일 삭제 중 오류 발생: ${error.message}"
            }

    }

}
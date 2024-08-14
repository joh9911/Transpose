package com.example.transpose.ui.screen.home.searchresult

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.transpose.ui.screen.home.HomeViewModel

@Composable
fun SearchResultScreen(
    viewModel: HomeViewModel
){
    val searchResults by viewModel.searchResults.collectAsState()
    val listState = rememberLazyListState()


    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()

    ) {
        if (searchResults.isNotEmpty()){
            items(searchResults.size){ index ->
                val item = searchResults[index]
                SearchResultItem(item = item, onClick = {})
            }
        }


    }

}
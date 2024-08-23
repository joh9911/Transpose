package com.example.transpose.ui.components.scrollbar

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.transpose.ui.components.items.LoadingIndicator


@Composable
internal fun <H, T> EndlessLazyColumn(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    hasMoreItems: Boolean,
    items: List<T>,
    headerData: H? = null,
    itemKey: (T) -> Any,
    headerContent: (@Composable (H) -> Unit)? = null,
    itemContent: @Composable (T) -> Unit,
    loadMore: () -> Unit
) {

    val reachedBottom: Boolean by remember { derivedStateOf { listState.reachedBottom() } }

    // load more if scrolled to bottom
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !loading && hasMoreItems) loadMore()
    }

    LazyColumn(modifier = modifier, state = listState) {
        if (headerData != null && headerContent != null) {
            item {
                headerContent(headerData)
            }
        }
        items(items.size){ index ->
            val item = items[index]
            itemContent(item)
            if (index == items.size - 1 && hasMoreItems)
                LoadingIndicator()
        }
    }
}


private fun LazyListState.reachedBottom(): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 && lastVisibleItem?.index == this.layoutInfo.totalItemsCount - 1
}
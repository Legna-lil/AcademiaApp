package com.example.academiaui.feature_search.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_search.data.PaperViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.arxiv.name.data.Entry

@Composable
fun PaperScreen(
    paperViewModel: PaperViewModel = viewModel(),
    papers: List<Entry>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onClick: (Entry) -> Unit = {}
) {
    val isRefreshing by paperViewModel.refreshingState
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    var lastLoadTime by remember { mutableStateOf(0L) }

    // 监听滚动到底部
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !isLoading) {
                    val lastVisibleItem = visibleItems.last()
                    val totalItems = listState.layoutInfo.totalItemsCount
                    // 当滚动到最后2个项目时触发加载更多
                     if (System.currentTimeMillis() - lastLoadTime > 1000 && !isRefreshing) {
                            lastLoadTime = System.currentTimeMillis()
                             if (lastVisibleItem.index >= totalItems - 2) {
                                 Log.i("Paper List", "Update")
                                 onLoadMore()
                             }
                        }

                }
            }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        PaperList(
            papers = papers,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            listState = listState,
            onClick = onClick
        )
    }
}
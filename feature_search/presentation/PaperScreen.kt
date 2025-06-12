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
import androidx.compose.runtime.derivedStateOf
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
    hasMore: Boolean, // 新增：是否有更多数据的标志
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onClick: (Entry) -> Unit = {}
) {
    val isRefreshing by paperViewModel.refreshingState
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val networkErrorState by paperViewModel.networkErrorState.collectAsState()

    // 使用衍生状态管理加载更多触发
    val loadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false

            if (papers.isEmpty()) return@derivedStateOf false

            Log.i("load More", "${!isLoading}, $hasMore, ${!isRefreshing}, ${totalItems > 0}")
            // 触发条件：最后一项可见 + 不在加载中 + 有更多数据 + 不在刷新中
            if (!isLoading &&
                hasMore &&
                !isRefreshing &&
                totalItems > 0
            ) {
                // 检查是否滚动到最后一个项目（带1项缓冲）
                Log.i("load More", "${lastVisibleItem.index >= totalItems - 1}")
                (lastVisibleItem.index >= totalItems - 1) // 当倒数第2项可见时触发
            } else {
                false
            }
        }
    }

    // 监听加载更多触发状态
    LaunchedEffect(loadMore) {
        if (loadMore) {
            Log.i("Paper List", "Triggering load more")
            onLoadMore()
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
            hasMore = hasMore,
            networkErrorState = networkErrorState,
            isRefreshing = isRefreshing,
            listState = listState,
            onClick = onClick
        )
    }
}
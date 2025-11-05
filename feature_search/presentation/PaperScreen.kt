package com.example.academiaui.feature_search.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    isRefreshing: Boolean,
    hasMore: Boolean, // 新增：是否有更多数据的标志
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onClick: (Entry) -> Unit = {}
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
    val networkErrorState by paperViewModel.networkErrorState.collectAsState()
    var lastLoadTime by remember { mutableLongStateOf(0L) }

    val shouldLoadMore by remember(listState, papers, isLoading, hasMore, isRefreshing) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            // 基本条件检查
            if (papers.isEmpty() || isLoading || isRefreshing || !hasMore) {
                Log.i("LoadMoreCheck", "条件不满足: papersEmpty=${papers.isEmpty()}, isLoading=$isLoading, isRefreshing=$isRefreshing, hasMore=$hasMore")
                return@derivedStateOf false
            }
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) {
                Log.i("LoadMoreCheck", "总项目数为0")
                return@derivedStateOf false
            }
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            // 检查是否滚动到最后一个项目（带2项缓冲）
            val shouldTrigger = lastVisibleItem.index >= totalItems - 1

            Log.i("LoadMoreCheck", "检查触发: lastIndex=${lastVisibleItem.index}, totalItems=$totalItems, threshold=${totalItems}, shouldTrigger=$shouldTrigger")

            shouldTrigger
        }
    }

    // 监听加载更多触发状态
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            val currentTime = System.currentTimeMillis()
            // 添加500ms防抖，防止连续触发
            if (currentTime - lastLoadTime > 1500L) {
                Log.i("Paper List", "Triggering load more")
                lastLoadTime = currentTime
                onLoadMore()
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
            hasMore = hasMore,
            isRefreshing = isRefreshing,
            listState = listState,
            onClick = onClick
        )
    }
}

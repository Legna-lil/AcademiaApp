package com.example.academiaui.feature_search.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.academiaui.core.util.showToast
import dev.arxiv.name.data.Entry


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperList(
    papers: List<Entry>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    hasMore: Boolean, // 是否有更多数据的标志
    networkErrorState: String?,
    listState: LazyListState = rememberLazyListState(),
    onClick: (Entry) -> Unit = {},
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val context = LocalContext.current
    val isUpdating by remember { derivedStateOf { papers.isNotEmpty() } }

    Log.d("Paper List", "PaperList recomposed")
    LaunchedEffect(isLoading, isRefreshing, isUpdating) {
        Log.i("Paper List", "isLoading $isLoading, isRefreshing $isRefreshing, isUpdating $isUpdating")
    }
    val showFullScreenLoading = remember(isLoading, isRefreshing, isUpdating) {
        (isLoading && !isUpdating) || isRefreshing
    }

    Box(modifier = modifier) {
        if(networkErrorState != null){
            showToast(context, networkErrorState)
        }
        if(showFullScreenLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState
            ) {
                if(papers.isEmpty()) {
                    item {
                        // 加载完成但无数据
                        Text(
                            "暂无数据",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    // 有数据时显示列表
                    items(papers) { paper ->
                        PaperCard(
                            paper = paper,
                            onClick = { onClick(paper) }
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                // 正在加载更多且还有数据
                                isLoading && hasMore -> CircularProgressIndicator()
                                // 没有更多数据
                                !hasMore -> Text("已经到底了！", color = Color.Gray)
                                // 其他情况显示占位符（最小高度）
                                else -> {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

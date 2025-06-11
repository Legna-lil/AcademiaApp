package com.example.academiaui.feature_search.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.arxiv.name.data.Entry


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperList(
    papers: List<Entry>,
    isLoading: Boolean,
    isRefreshing: Boolean,
    listState: LazyListState = rememberLazyListState(),
    onClick: (Entry) -> Unit = {},
    modifier: Modifier = Modifier.fillMaxSize()
) {

    Log.d("Paper List", "PaperList recomposed")

    Box(modifier = modifier) {
        if (isLoading || isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
            return@Box
        }
        when {
            papers.isEmpty() -> {
                // 加载完成但无数据
                Text(
                    "暂无数据",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                // 有数据时显示列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState
                ) {
                    items(papers) { paper ->
                        PaperCard(
                            paper = paper,
                            onClick = { onClick(paper) }
                        )
                        // 4. 增量加载指示器
                        if (paper == papers.last() && isLoading) {
                            Log.i("Paper List", "Update New Page")
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

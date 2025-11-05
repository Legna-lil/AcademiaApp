package com.example.academiaui.feature_search.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.ui.components.TopSearchEngine
import com.example.academiaui.feature_search.data.PaperViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun SearchPage(
    paperViewModel: PaperViewModel = viewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    var currentQuery by remember { mutableStateOf("") }
    val searchPapers by paperViewModel.searchPapers.collectAsState()
    val isLoading by paperViewModel.searchLoadingState
    val searchRefreshing by paperViewModel.searchRefreshingState

    Column(modifier = Modifier.fillMaxSize()
        .padding(10.dp)) {
        TopSearchEngine(onSearch = { query ->
            coroutineScope.launch {
                paperViewModel.searchPapers(query)
            }
            currentQuery = query
        })
        PaperScreen(
            paperViewModel = paperViewModel,
            papers = searchPapers,
            isLoading = isLoading,
            isRefreshing = searchRefreshing,
            hasMore = paperViewModel.canLoadMore.value,
            onRefresh = {
                if(currentQuery != "") {
                    paperViewModel.refreshSearchPapers(currentQuery)
                }
            },
            onLoadMore = {
                paperViewModel.loadMorePapers(currentQuery)
            }
        )
    }
}
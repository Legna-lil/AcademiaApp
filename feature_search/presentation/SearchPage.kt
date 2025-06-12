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
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.ui.components.TopSearchEngine
import com.example.academiaui.feature_search.data.PaperViewModel

@Composable
fun SearchPage(
    paperViewModel: PaperViewModel = viewModel(),
) {
    var currentQuery by remember { mutableStateOf("") }
    val searchPapers by paperViewModel.searchPapers.collectAsState()
    val isLoading by paperViewModel.searchLoadingState

    Column(modifier = Modifier.fillMaxSize()
        .padding(10.dp)) {
        TopSearchEngine(onSearch = { query ->
            paperViewModel.searchPapers(query)
            currentQuery = query
        })
        PaperScreen(
            paperViewModel = paperViewModel,
            papers = searchPapers,
            isLoading = isLoading,
            hasMore = paperViewModel.canLoadMore.value,
            onRefresh = {
                paperViewModel.refreshSearchPapers(currentQuery)
            },
            onLoadMore = {
                paperViewModel.loadMorePapers(currentQuery)
            }
        )
    }
}
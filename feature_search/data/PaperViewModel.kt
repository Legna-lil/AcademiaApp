package com.example.academiaui.feature_search.data

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academiaui.feature_manager.data.model.UserDataStore
import com.example.academiaui.feature_search.repository.PaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arxiv.name.data.Entry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaperViewModel @Inject constructor(
    private val paperRepository: PaperRepository,
    private val userDataStore: UserDataStore
): ViewModel() {

    private val _homePapers = MutableStateFlow<List<Entry>>(emptyList())
    val homePapers: StateFlow<List<Entry>> get() = _homePapers

    private val _searchPapers = MutableStateFlow<List<Entry>>(emptyList())
    val searchPapers: StateFlow<List<Entry>> get()  = _searchPapers

    internal val _homeLoadingState = mutableStateOf(false)
    val homeLoadingState: State<Boolean> get() = _homeLoadingState

    internal val _searchLoadingState = mutableStateOf(false)
    val searchLoadingState: State<Boolean> get() = _searchLoadingState

    private val _refreshingState = mutableStateOf(false)
    val refreshingState: State<Boolean> get() = _refreshingState

    private var currentPage = 0
    private val pageSize = 10
    private var canLoadMore = true

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun defaultPapers() {
        viewModelScope.launch {
            _homeLoadingState.value = true
            val papersFlow: Flow<List<Entry>?> = userDataStore.preferredField
                    .flatMapLatest { categories ->
                        flowOf(paperRepository.defaultPapers(categories))
                    }
            papersFlow.collect { papers ->
                if(papers == null) {
                    _homePapers.value = emptyList<Entry>()
                } else {
                    _homePapers.value = papers
                }
                _homeLoadingState.value = false
            }
        }
    }

    fun refreshHomePapers() {
        if (refreshingState.value) return
        _refreshingState.value = true
        try {
            defaultPapers()
        } catch (e: Exception) {
            Log.e("Paper View Model", "Refresh Exception")
        } finally {
            _refreshingState.value = false
        }
    }

    fun refreshSearchPapers(query: String) {
        if (refreshingState.value) return
        _refreshingState.value = true
        try {
            searchPapers(query)
        } catch (e: Exception) {
            Log.e("Paper View Model", "Refresh Exception")
        } finally {
            _refreshingState.value = false
        }
    }

    fun searchPapers(query: String) {
        viewModelScope.launch {
            _searchLoadingState.value = true
            var papers = paperRepository.searchPapers(query)
            if(papers == null) {
                _searchPapers.value = emptyList<Entry>()
            } else {
                _searchPapers.value = papers
            }
            _searchLoadingState.value = false
        }
    }

    // 加载更多论文
    fun loadMorePapers(query: String) {
        if (!canLoadMore) return
        viewModelScope.launch {
            try {
                val startIndex = currentPage * pageSize
                // 构建分页请求
                val newPapers = paperRepository.searchPapersByPaging(query, startIndex, pageSize)
                if(newPapers != null) {
                    if (newPapers.size < pageSize) {
                        canLoadMore = false // 已加载所有数据
                    }
                    _searchPapers.value = _searchPapers.value + newPapers
                    currentPage++
                }

            } catch (e: Exception) {
                Log.e("Search Engine", "Paging " + e.toString())
            }
        }
    }

    // 刷新数据
    fun refreshPapers(query: String) {
        if (refreshingState.value) return
        _refreshingState.value = true
        try {
            currentPage = 0
            canLoadMore = true
            _searchPapers.value = emptyList()
            loadMorePapers(query)
        } catch (e: Exception) {
            Log.e("Paper View Model", "Refresh Exception")
        } finally {
            _refreshingState.value = false
        }
    }
}

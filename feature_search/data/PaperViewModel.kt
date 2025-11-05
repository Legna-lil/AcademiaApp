package com.example.academiaui.feature_search.data

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.academiaui.core.network.NetworkService
import com.example.academiaui.feature_manager.data.model.UserDataStore
import com.example.academiaui.feature_search.repository.PaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arxiv.name.data.Entry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.toString

@HiltViewModel
class PaperViewModel @Inject constructor(
    private val paperRepository: PaperRepository,
    private val userDataStore: UserDataStore,
    private val networkService: NetworkService
): ViewModel() {

    private val _homePapers = MutableStateFlow<List<Entry>>(emptyList())
    val homePapers: StateFlow<List<Entry>> get() = _homePapers

    private val _searchPapers = MutableStateFlow<List<Entry>>(emptyList())
    val searchPapers: StateFlow<List<Entry>> get()  = _searchPapers

    internal val _homeLoadingState = mutableStateOf(false)
    val homeLoadingState: State<Boolean> get() = _homeLoadingState

    internal val _searchLoadingState = mutableStateOf(false)
    val searchLoadingState: State<Boolean> get() = _searchLoadingState

    private val _homeRefreshingState = mutableStateOf(false)
    val homeRefreshingState: State<Boolean> get() = _homeRefreshingState

    private val _searchRefreshingState = mutableStateOf(false)
    val searchRefreshingState: State<Boolean> get() = _searchRefreshingState

    private var currentPage = 0
    private val pageSize = 10
    private val _canLoadMore = mutableStateOf(true)
    val canLoadMore: State<Boolean> get() = _canLoadMore

    // 网络错误状态：null或者错误string
    private val _networkErrorState = MutableStateFlow<String?>(null)
    val networkErrorState: StateFlow<String?> get() = _networkErrorState

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun defaultPapers() {
        _homeLoadingState.value = true
        val (result, state) = networkService.withNetworkTimeout {
            userDataStore.preferredField
                .flatMapLatest { categories ->
                    flowOf(paperRepository.defaultPapers(categories))
                }.first()
        }
        handleNetworkState(state)
        Log.i("Default Papers", result.toString())
        if (result == null) {
            _homePapers.value = emptyList<Entry>()
        } else {
            _homePapers.value = result
        }
        _homeLoadingState.value = false
    }

    fun refreshHomePapers() {
        if (homeRefreshingState.value) return
        _homeRefreshingState.value = true
        viewModelScope.launch {
            Log.i("Refresh", _homeRefreshingState.value.toString())
            try {
                defaultPapers()
            } catch (e: Exception) {
                Log.e("Paper View Model", "Refresh Exception")
            }
            Log.i("Paper ViewModel", "Home Refreshed")
            _homeRefreshingState.value = false
        }

    }

    suspend fun searchPapers(query: String) {
        _searchLoadingState.value = true
        _networkErrorState.value = null
        val (papers, state) = networkService.withNetworkTimeout {
            paperRepository.searchPapers(query)
        }
        handleNetworkState(state)
        if(papers == null) {
            _searchPapers.value = emptyList<Entry>()
        } else {
            _searchPapers.value = papers
        }
        _searchLoadingState.value = false
    }

    fun refreshSearchPapers(query: String) {
        if (searchRefreshingState.value) return
        _searchLoadingState.value = true
        viewModelScope.launch {
            try {
                searchPapers(query)
            } catch (e: Exception) {
                Log.e("Paper ViewModel", "Refresh Exception")
            } finally {
                Log.i("Paper ViewModel", "Search Refreshed")
                _searchRefreshingState.value = false
            }
        }


    }
    // 加载更多论文
    fun loadMorePapers(query: String) {
        if (!_canLoadMore.value) return
        _searchLoadingState.value = true
        _networkErrorState.value = null
        viewModelScope.launch {
            try {
                val startIndex = currentPage * pageSize
                // 构建分页请求
                val (newPapers, state) = networkService.withNetworkTimeout {
                    paperRepository.searchPapersByPaging(query, startIndex, pageSize)
                }
                handleNetworkState(state)

                if(newPapers != null) {
                    if (newPapers.size < pageSize) {
                        _canLoadMore.value = false // 已加载所有数据
                    }
                    _searchPapers.value = _searchPapers.value + newPapers
                    currentPage++
                }
                Log.i("Search Engine", "Finish Paging Search")
                _searchLoadingState.value = false
            } catch (e: Exception) {
                Log.e("Search Engine", "Paging $e")
            }
        }
    }

    // 处理网络状态
    private fun handleNetworkState(state: NetworkService.NetworkState) {
        _networkErrorState.value = when (state) {
            NetworkService.NetworkState.Unavailable -> "网络不可用，请检查连接"
            NetworkService.NetworkState.TimeoutWarning -> "网络响应缓慢..."
            NetworkService.NetworkState.TimeoutCancel -> "请求超时，请重试"
            NetworkService.NetworkState.Error -> "出现异常，请重试"
            else -> null
        }
    }
}
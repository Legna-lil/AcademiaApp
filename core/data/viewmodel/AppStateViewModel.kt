package com.example.academiaui.core.data.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.academiaui.core.data.AppState
import com.example.academiaui.core.util.convertArxivUrl
import com.example.academiaui.feature_db.entities.Download
import com.example.academiaui.feature_db.entities.ListItem
import com.example.academiaui.feature_db.entities.Record
import com.example.academiaui.feature_db.entities.Star
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arxiv.name.data.Entry
import javax.inject.Inject

@HiltViewModel
class AppStateViewModel @Inject constructor(): ViewModel() {

    private val _currentAppState = mutableStateOf(AppState.HOME)
    val currentAppState: State<AppState>  get() = _currentAppState

    private val _selectedPaper = mutableStateOf<Entry?>(null)
    val selectedPaper: State<Entry?> get() =  _selectedPaper

    // 针对数据库条目的url来确定阅读器读取的pdf，但是有些冗长
    private val _selectedPaperPath = mutableStateOf<String>("")
    val selectedPaperPath: State<String> get() = _selectedPaperPath

    private val _selectedPaperType = mutableStateOf<String>("url")
    val selectedPaperType: State<String> get() = _selectedPaperType

    private val _lastAppState = mutableStateOf(AppState.HOME)
    // 问题出现在：从PROFILE->READER->PROFILE，再返回，会丢失状态信息
    private val _rootAppState = mutableStateOf(AppState.HOME)

    // 最底层AppState用到的方法：HOME, SEARCH, MANAGER
    fun setAppState(appState: AppState) {
        _currentAppState.value = appState
        _rootAppState.value = appState
        Log.i("AppState Changed", appState.toString())
    }

    // 更新选中的论文并自动切换状态
    fun selectPaperAndNavigate(paper: Entry?) {
        _selectedPaper.value = paper!!
        _selectedPaperPath.value = convertArxivUrl(paper!!.id)
        _lastAppState.value = _currentAppState.value
        Log.i("Select Paper", _selectedPaper.value.toString())
        _currentAppState.value = AppState.PROFILE
    }

    // 返回根界面
    fun navigateBack() {
        if(_selectedPaperPath.value != "")
            _selectedPaperPath.value = ""
        if(_selectedPaper.value != null)
            _selectedPaper.value = null
        _currentAppState.value = _rootAppState.value
    }

    fun readPaper() {
        try {
            _lastAppState.value = _currentAppState.value
            // 日后优化
            _selectedPaperType.value = "url"
            _currentAppState.value = AppState.READER
        } catch(e: Exception) {
            Log.e("Reader", e.message.toString())
        }
    }

    fun backFromPaper() {
        try {
            _currentAppState.value = _lastAppState.value
        } catch (e: Exception) {
            Log.e("Reader", e.message.toString())
        }
    }

    fun manageSetting() {
        try {
            _currentAppState.value = AppState.MANAGER
        } catch (e: Exception) {
            Log.e("Manager", e.message.toString())
        }
    }

    fun selectPaperInManager(paper: ListItem) {
        _lastAppState.value = _currentAppState.value
        when(paper) {
            is Record, is Star -> {
                _selectedPaperPath.value = paper.url
                _selectedPaperType.value = "url"
            }
            is Download -> {
                _selectedPaperPath.value = paper.uri
                _selectedPaperType.value = "uri"
            }
        }
        Log.i("TEST-SelectInManager", paper.toString())
        _currentAppState.value = AppState.READER
    }

}
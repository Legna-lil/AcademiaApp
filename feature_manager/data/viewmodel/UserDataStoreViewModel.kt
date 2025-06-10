package com.example.academiaui.feature_manager.data.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academiaui.feature_manager.util.SubjectMapper
import com.example.academiaui.feature_manager.data.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataStoreViewModel @Inject constructor(
    private val userDataStore: UserDataStore
) : ViewModel() {

    // UI 状态
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    private val _selectedFields = MutableStateFlow<List<String>>(emptyList())
    val selectedFields: StateFlow<List<String>> = _selectedFields.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Controls when collection starts and stops
        initialValue = emptyList() // Initial value for the StateFlow
    )

    val categorizedAvailableFields: Map<String, List<Pair<String, String>>> =
        SubjectMapper.getAllCategories()

    // 加载用户数据
    init {
        viewModelScope.launch {
            userDataStore.username.collect {
                _username.value = it!!
            }
        }

        viewModelScope.launch {
            userDataStore.avatar.collect { avatar ->
                _avatarUri.value = avatar?.let {
                    Uri.parse(it)
                }
            }
        }

        viewModelScope.launch {
            userDataStore.preferredField.collect {
                _selectedFields.value = it
            }
        }

        Log.i("Setting", categorizedAvailableFields.toString())
    }

    // 更新用户名
    fun updateUsername(newName: String) {
        viewModelScope.launch {
            userDataStore.saveUsername(newName)
        }
    }

    // 更新头像
    fun updateAvatar(newAvatar: String) {
        viewModelScope.launch {
            userDataStore.saveAvatar(newAvatar)
        }
    }

    // 更新偏好领域
    fun toggleField(field: String) {
        val currentInterests = _selectedFields.value.toMutableList()
        if (currentInterests.contains(field)) {
            currentInterests.remove(field)
        } else {
            currentInterests.add(field)
        }
        _selectedFields.value = currentInterests
        viewModelScope.launch {
            userDataStore.savePreferredField(currentInterests)
        }
    }
}
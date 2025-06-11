package com.example.academiaui.feature_manager.data.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 为 Context 创建扩展属性
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_info")

// 定义用户数据键
object UserPreferencesKeys {
    val USERNAME = stringPreferencesKey("username")
    val AVATAR = stringPreferencesKey("avatar")
    val PREFERRED_FIELD = stringPreferencesKey("preferred_field")
}

// 用户数据存储类
class UserDataStore(private val context: Context) {

    // 获取用户名
    val username: Flow<String?> = context.userDataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.USERNAME] ?: "Default User"
        }

    // 获取头像
    val avatar: Flow<String?> = context.userDataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.AVATAR]
        }

    // 获取偏好领域
    val preferredField: Flow<List<String>> =
        context.userDataStore.data.map { preferences ->
            val field = preferences[UserPreferencesKeys.PREFERRED_FIELD] ?: ""
            if(field.isEmpty()) emptyList() else field.split(",").map { it.trim() }
        }

    // 保存用户名
    suspend fun saveUsername(username: String) {
        context.userDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USERNAME] = username
        }
    }

    // 保存头像
    suspend fun saveAvatar(avatar: String) {
        context.userDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.AVATAR] = avatar
        }
    }

    // 保存偏好领域
    suspend fun savePreferredField(interests: List<String>) {
        context.userDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.PREFERRED_FIELD] = interests.joinToString(",")
        }
    }

    // 清除所有用户数据
    suspend fun clearUserData() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
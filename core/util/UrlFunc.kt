package com.example.academiaui.core.util

import android.content.Context
import android.widget.Toast

// 转换为指向目标pdf的https路径
fun convertArxivUrl(originalUrl: String): String {
    return originalUrl
        .replace("http://", "https://")    // 替换协议
        .replace("/abs/", "/pdf/")          // 替换路径
}

fun modifyTitle(title: String): String {
    return title.replace(Regex("\\s+"), " ").trim()
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
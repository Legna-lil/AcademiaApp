package com.example.academiaui.core.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ConfirmationDialog(
    showDialog: Boolean, // 控制对话框显示/隐藏
    onDismissRequest: () -> Unit, // 点击对话框外部或返回键时触发
    title: String,
    text: String,
    confirmButtonText: String,
    onConfirm: () -> Unit, // 点击确认按钮时触发
    dismissButtonText: String = "取消", // 默认取消按钮文本
    onDismiss: () -> Unit = onDismissRequest, // 默认取消操作就是 dismiss
    confirmButtonColors: ButtonColors = ButtonDefaults.textButtonColors() // 确认按钮颜色，可自定义
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = confirmButtonColors // 应用自定义颜色
                ) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(dismissButtonText)
                }
            }
        )
    }
}

@Composable
fun temp(

){
    var showUnstarDialog by remember { mutableStateOf(false) }
    // 取消收藏确认对话框
    if (showUnstarDialog) {
        AlertDialog(
            onDismissRequest = { showUnstarDialog = false },
            title = { Text("取消收藏") },
            text = { Text("确定要取消收藏 \"\" 吗？") },
            confirmButton = {
                TextButton(
                    onClick = {/*onUnstarConfirm*/},
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showUnstarDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}
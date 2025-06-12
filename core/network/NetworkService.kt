package com.example.academiaui.core.network

import android.content.Context
import android.util.Log
import com.example.academiaui.core.util.showToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

import javax.inject.Inject

class NetworkService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 网络状态密封类
    sealed class NetworkState {
        object Available : NetworkState()
        object Unavailable : NetworkState()
        object TimeoutWarning : NetworkState()  // 15秒超时
        object TimeoutCancel : NetworkState()   // 30秒超时
        object Error : NetworkState()
        object Success : NetworkState()
    }

    // 检查网络连接
    fun checkNetwork(): Boolean {
        val isAvailable = NetworkConnectivity.isNetworkAvailable(context)
        return isAvailable
    }

    // 带超时处理的网络请求
    suspend fun <T> withNetworkTimeout(
        block: suspend () -> T
    ): Pair<T?, NetworkState> {
        Log.i("Network Service", "Available: " + checkNetwork())
        if (!checkNetwork()) {
            return Pair(null, NetworkState.Unavailable)
        }
        return try {
            // 使用coroutineScope来管理子协程
            coroutineScope {
                // 创建15秒超时警告的Channel
                val warningChannel = Channel<Unit>(Channel.RENDEZVOUS)
                // 启动15秒警告任务
                val warningJob = launch {
                    delay(10000L)
                    if (isActive) {
                        warningChannel.send(Unit)
                        Log.i("Network Service", "Warning: 5s")
                    }
                }
                val deferredResult = async { block() }
                launch {
                    for (unit in warningChannel) {
                        // 可以在这里做一些“警告”相关的操作，例如打印日志或通知UI
                        Log.i("Network Service", "Received 10s warning notification.")
                        // 你也可以在这里调用一个回调函数，通知外部有警告发生
                        showToast(context, "网络响应缓慢...")
                    }
                }
                // 使用withTimeoutOrNull 来处理20秒超时，等待 deferredResult 完成
                val timeoutResult = withTimeoutOrNull(20000L) { // 20秒最终超时
                    val result = deferredResult.await() // 等待实际的网络请求完成
                    // 如果请求在20秒内完成，取消警告任务和通知通道
                    warningJob.cancel()
                    warningChannel.close() // 关闭通道，停止监听
                    Log.i("Network Service", "Job Finished")
                    Pair(result, NetworkState.Success)
                }
                // 任务完成或超时
                warningJob.cancel() // 确保警告任务被取消（防止竞态条件）
                warningChannel.close() // 确保通知通道被关闭
                when (timeoutResult) {
                    null -> {
                        // 只有当 withTimeoutOrNull 自身超时，而 select 还没有返回时，timeoutResult 才是 null
                        Log.i("Network Service", "TimeOut: 20s")
                        Pair(null, NetworkState.TimeoutCancel)
                    }
                    else -> {
                        // select 表达式返回了一个 Pair
                        Log.i("Network Service", "Timeout Result != null")
                        timeoutResult
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Network Service", e.message.toString())
            Pair(null, NetworkState.Error)
        }
    }
}
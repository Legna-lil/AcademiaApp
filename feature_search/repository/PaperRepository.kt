package com.example.academiaui.feature_search.repository

import android.util.Log
import dev.arxiv.name.data.Entry
import dev.arxiv.name.data.Feed
import dev.arxiv.name.options.SearchField
import dev.arxiv.name.options.SortBy
import dev.arxiv.name.options.SortOrder
import dev.arxiv.name.requests.SearchRequest
import dev.arxiv.name.requests.SearchRequestExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaperRepository @Inject constructor() {

    suspend fun searchPapers(title: String): List<Entry>? {
        val request: SearchRequest = SearchRequest.SearchRequestBuilder
            .create(title, SearchField.TITLE)
            .build()
        val response: Feed = withContext(Dispatchers.IO) {
            SearchRequestExecutor().executeAndMap(request)
        }
        Log.i("Search Engine", request.toString())
        response.entry?.forEach {
            Log.i("Search Engine", it.toString())
        }
        return response.entry
    }


    suspend fun defaultPapers(categories: List<String?>) : List<Entry>? {
        Log.i("Search Engine", categories.toString())
        if (categories.isEmpty()) {
            Log.w("Search Engine", "Category list is empty.")
            // 暂时先指定cs.AI
        }

        // 1. 获取第一个元素用于 create()
        val firstCategory = when {
            categories.isEmpty() -> "cs.AI"
            else -> categories.first()
        }
        var requestBuilder = SearchRequest.SearchRequestBuilder
            .create(firstCategory!!, SearchField.SUBJECT_CATEGORY)

        // 2. 遍历其余元素，使用 or() 级联调用
        if (categories.size > 1) {
            // 从第二个元素开始遍历
            categories.subList(1, categories.size).forEach { category ->
                requestBuilder = requestBuilder.or(category!!, SearchField.SUBJECT_CATEGORY)
            }
        }

        // 3. 继续构建其他通用的请求参数
        val request = requestBuilder
            .sortOrder(SortOrder.DESCENDING)
            .sortBy(SortBy.LAST_UPTATED_DATE)
            .maxResults(20)
            .build()
        Log.i("Search Engine", "On Search")
        val response: Feed = withContext(Dispatchers.IO) {
                Log.i("Search Engine", "in IO")
                SearchRequestExecutor().executeAndMap(request)
            }
        response.entry?.forEach {
            Log.i("Search Engine", it.toString())
        }
        return response.entry
    }

    suspend fun searchPapersByPaging(query: String, startIndex: Int, pageSize: Int): List<Entry>? {
        val request = SearchRequest.SearchRequestBuilder
            .create(query, SearchField.ALL)
            .start(startIndex)
            .maxResults(pageSize)
            .build()
        Log.i("Search Engine", "On Search Paging")
        val response: Feed = withContext(Dispatchers.IO) {
            Log.i("Search Engine", "in IO")
            SearchRequestExecutor().executeAndMap(request)
        }
        response.entry?.forEach {
            Log.i("Search Engine", it.toString())
        }
        return response.entry
    }
}
package com.example.academiaui.feature_manager.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.academiaui.feature_db.entities.ListItem

@Composable
fun ManageList(
    items: List<ListItem>,
    isManageMode: Boolean,
    isLoading: Boolean = false,
    onClick: (ListItem) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Box(modifier = modifier) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            items.isEmpty() -> {
                Text(
                    "No items found",
                    modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        count = items.size
                    ) {item ->
                        Manage(
                            item = items[item],
                            isManageMode = isManageMode,
                            onClick = {
                                onClick(items[item])
                            })
                    }
                }
            }
        }
    }
}
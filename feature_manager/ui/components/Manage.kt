package com.example.academiaui.feature_manager.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.feature_db.entities.Download
import com.example.academiaui.feature_db.entities.Record
import com.example.academiaui.feature_db.entities.Star
import com.example.academiaui.feature_db.entities.ListItem
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel


@Composable
fun Manage(
    managerViewModel: ManagerViewModel = viewModel(),
    item: ListItem,
    isManageMode: Boolean,
    onClick: (ListItem) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isManageMode) {
                    managerViewModel.toggleSelection(item.url)
                } else {
                    onClick(item)
                    if(item is Download) {
                        Log.i("Database", item.uri)
                    }

                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isManageMode) {
            Checkbox(
                checked = managerViewModel.selectedUrls.contains(item.url),
                onCheckedChange = {
                    managerViewModel.toggleSelection(item.url) },
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        // 记录信息
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(2.dp))
            when (item) {
                is Record -> {
                    Text(text = item.viewedTime.toString(),
                        style = MaterialTheme.typography.labelSmall)
                }
                is Download -> {
                    Text(text = "Updated:" + item.updatedTime.toString(),
                        style = MaterialTheme.typography.labelSmall)
                    Text(text = "DOWNLOAD:" + item.downloadTime.toString(),
                        style = MaterialTheme.typography.labelSmall)
                }
                is Star -> {
                    Text(text = item.starredTime.toString(),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
package com.example.academiaui.feature_manager.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.core.util.modifyTitle
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel

@Composable
fun PaperProfile(
    appStateViewModel: AppStateViewModel = viewModel(),
    managerViewModel: ManagerViewModel = viewModel(),
) {
    val paper = appStateViewModel.selectedPaper.value!!

    Row() {
        IconButton(onClick = {
            appStateViewModel.navigateBack()
        }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }
    Column(Modifier.fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = modifyTitle(paper.title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = paper.author.joinToString { author -> author.name },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Published Date: " + paper.published.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Summary: " + modifyTitle(paper.summary),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.weight(.1f))
        Button( onClick = {
            appStateViewModel.readPaper()
            managerViewModel.record(paper)
        }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Outlined.ImportContacts, contentDescription = "预览")
                Text("预览")
            }
        }
        Spacer(modifier = Modifier.weight(.2f))
        ButtonDownload(
            paper = paper,
            managerViewModel = managerViewModel
        )
        Spacer(modifier = Modifier.weight(.2f))
        ButtonStar(
            paper = paper,
            managerViewModel = managerViewModel
        )
        Spacer(modifier = Modifier.weight(.1f))
    }
}

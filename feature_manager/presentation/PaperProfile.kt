package com.example.academiaui.feature_manager.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ImportContacts
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
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
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "发布日期：" + paper.published.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "最新更新日期：" + paper.updated.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
            ) {
                Text(
                    text = "概要：" + modifyTitle(paper.summary),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(20.dp)
                )
            }

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
        Spacer(modifier = Modifier.height(100.dp))
    }
}
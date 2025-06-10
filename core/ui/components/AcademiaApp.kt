package com.example.academiaui.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.academiaui.core.data.AppState
import com.example.academiaui.feature_search.data.PaperViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.academiaui.core.data.viewmodel.AppStateViewModel
import com.example.academiaui.feature_agent.presentation.AgentFAB
import com.example.academiaui.feature_agent.presentation.ChatBox
import com.example.academiaui.feature_agent.viewmodel.AgentViewModel
import com.example.academiaui.feature_manager.data.viewmodel.ManagerViewModel
import com.example.academiaui.feature_search.presentation.PdfViewer
import com.example.academiaui.feature_search.presentation.SearchPage
import com.example.academiaui.feature_manager.ui.components.Manager
import com.example.academiaui.feature_manager.ui.components.PaperProfile
import com.example.academiaui.feature_search.presentation.PaperScreen
import dev.arxiv.name.data.Entry

@Composable
fun AcademiaApp(
    appStateViewModel: AppStateViewModel = viewModel(),
    paperViewModel: PaperViewModel = viewModel(),
    managerViewModel: ManagerViewModel = viewModel(),
    agentViewModel: AgentViewModel = viewModel()
) {
    var currentAppState: AppState = appStateViewModel.currentAppState.value
    var lastLoadTime by remember { mutableStateOf(0L) }

    val homePapers: List<Entry> by paperViewModel.homePapers.collectAsState()
    val isLoading: Boolean by paperViewModel.homeLoadingState

    var showChatDialog by agentViewModel.showChatDialog

    LaunchedEffect(Unit) {
        paperViewModel.defaultPapers()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                when (currentAppState) {
                    AppState.HOME -> HomeTopBar()
                    AppState.SEARCH, AppState.PERSON -> null
                    else -> null
                }
            },
            bottomBar = {
                when (currentAppState) {
                    AppState.HOME, AppState.SEARCH, AppState.PERSON ->
                        BottomNavigation(appStateViewModel)
                    else -> null
                }
            },
            floatingActionButton = {
                when(currentAppState) {
                    AppState.HOME, AppState.SEARCH ->
                        AgentFAB(agentViewModel)
                    else ->
                        null
                }
            },
            content = {
                Column (Modifier.fillMaxSize()
                    .padding(it)
//                    .background(Color.LightGray)
                ) {
                    when (currentAppState) {
                        AppState.HOME -> {
                            PaperScreen(
                                paperViewModel = paperViewModel,
                                papers = homePapers,
                                isLoading = isLoading,
                                onRefresh = {
                                    if (System.currentTimeMillis() - lastLoadTime > 1000) {
                                        lastLoadTime = System.currentTimeMillis()
                                        paperViewModel.refreshHomePapers()
                                    }
                                })
                        }
                        AppState.SEARCH -> {
                            SearchPage(paperViewModel)
                        }
                        AppState.PERSON -> {
                            UserProfile(appStateViewModel, managerViewModel)
                        }
                        AppState.PROFILE -> {
                            PaperProfile(appStateViewModel, managerViewModel)
                        }
                        AppState.READER -> {
                            PdfViewer(appStateViewModel, agentViewModel, managerViewModel)
                        }
                        AppState.MANAGER -> {
                            Manager(appStateViewModel, managerViewModel)
                        }
                    }
                }
            }

        )
        if(showChatDialog) {
            ChatBox(agentViewModel, onDismiss = {
                agentViewModel.toggleShowDialog()
            })
        }
    }
}

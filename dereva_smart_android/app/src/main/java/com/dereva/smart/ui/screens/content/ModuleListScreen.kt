package com.dereva.smart.ui.screens.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.domain.model.Module
import com.dereva.smart.domain.model.ModuleStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleListScreen(
    navController: NavController,
    viewModel: ContentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val authViewModel: com.dereva.smart.ui.screens.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Learning Modules")
                        currentUser?.let {
                            Text(
                                text = if (it.isGuestMode) "Browse all categories" else "Category: ${it.targetCategory.name}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            // Debug: Show subscription status
                            if (!it.isGuestMode) {
                                Text(
                                    text = "Status: ${it.subscriptionStatus.name} | Active: ${it.isSubscriptionActive}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (it.isSubscriptionActive) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Category filter for guests
                    if (currentUser?.isGuestMode == true) {
                        IconButton(onClick = { navController.navigate("category_selection") }) {
                            Icon(Icons.Default.Add, "Change Category")
                        }
                    }
                }
            )
        }
    ) { padding ->
        // Subscription Required Dialog
        if (uiState.showSubscriptionRequired) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissSubscriptionDialog() },
                title = { Text(if (uiState.isGuestMode) "Account Required" else "Premium Content") },
                text = { 
                    Text(if (uiState.isGuestMode) "This content requires an account. Please login or register to continue." else "This content requires a premium subscription to access.")
                },
                confirmButton = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.isGuestMode) {
                            TextButton(
                                onClick = {
                                    viewModel.dismissSubscriptionDialog()
                                    navController.navigate("register")
                                }
                            ) {
                                Text("Register")
                            }
                            TextButton(
                                onClick = {
                                    viewModel.dismissSubscriptionDialog()
                                    navController.navigate("login")
                                }
                            ) {
                                Text("Login")
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    viewModel.dismissSubscriptionDialog()
                                    navController.navigate("payment")
                                }
                            ) {
                                Text("Subscribe Now")
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.dismissSubscriptionDialog() }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Card
            uiState.contentStats?.let { stats ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Your Progress",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${stats.completedModules}/${stats.totalModules}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Modules",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column {
                                Text(
                                    text = "${stats.completedLessons}/${stats.totalLessons}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Lessons",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column {
                                Text(
                                    text = "${stats.totalStudyTime}m",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Study Time",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
            
            // Content area with loading overlay
            Box(modifier = Modifier.weight(1f)) {
                // Show message if no modules and not loading
                if (uiState.modules.isEmpty() && !uiState.isLoading && currentUser != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No modules available",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Check your internet connection and try again",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Modules List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.modules) { module ->
                        ModuleCard(
                            module = module,
                            onModuleClick = {
                                viewModel.selectModule(module.id)
                                navController.navigate("lesson_list/${module.id}")
                            },
                            onDownloadClick = {
                                viewModel.downloadModule(module.id)
                            }
                        )
                    }
                }
                
                // Loading Overlay
                if (uiState.isLoading || currentUser == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (currentUser == null) "Initializing..." else "Loading modules...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            // Error
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleCard(
    module: Module,
    onModuleClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        onClick = onModuleClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (module.requiresSubscription) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Premium",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Status Icon
                when (module.status) {
                    ModuleStatus.LOCKED -> {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        if (!module.isDownloaded) {
                            IconButton(onClick = onDownloadClick) {
                                Icon(Icons.Default.Add, "Download")
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Module Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${module.lessonCount} lessons",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${module.estimatedDuration} min",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${module.downloadSize / (1024 * 1024)} MB",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Progress Bar
            if (module.status == ModuleStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { module.completionPercentage / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

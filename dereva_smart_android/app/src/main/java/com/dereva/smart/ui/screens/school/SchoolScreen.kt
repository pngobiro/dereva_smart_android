package com.dereva.smart.ui.screens.school

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolScreen(
    navController: NavController,
    viewModel: SchoolViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLinkDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Sync") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.linkedSchool == null) {
                // Not linked to any school
                NoSchoolLinkedView(
                    onLinkSchool = { showLinkDialog = true }
                )
            } else {
                // Linked to a school
                SchoolLinkedView(
                    uiState = uiState,
                    onUnlink = { viewModel.unlinkFromSchool() },
                    onToggleSharing = { viewModel.toggleProgressSharing(it) },
                    onShareProgress = { viewModel.shareProgress() }
                )
            }
            
            // Error/Success messages
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                uiState.successMessage?.let { message ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
    
    if (showLinkDialog) {
        LinkSchoolDialog(
            onDismiss = { showLinkDialog = false },
            onLink = { code ->
                viewModel.linkToSchool(code)
                showLinkDialog = false
            }
        )
    }
}

@Composable
fun NoSchoolLinkedView(onLinkSchool: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏫",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Not Linked to a School",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Link to your driving school to access scheduled modules and share your progress",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onLinkSchool,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Link to School")
        }
    }
}

@Composable
fun SchoolLinkedView(
    uiState: SchoolUiState,
    onUnlink: () -> Unit,
    onToggleSharing: (Boolean) -> Unit,
    onShareProgress: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // School Info Card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.linkedSchool?.name ?: "Unknown School",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (uiState.linkedSchool?.isVerified == true) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    Text("Code: ${uiState.linkedSchool?.code}")
                    Text("Location: ${uiState.linkedSchool?.location}")
                    Text("Students: ${uiState.linkedSchool?.totalStudents}")
                    Text("Pass Rate: ${String.format("%.1f", uiState.linkedSchool?.averagePassRate ?: 0.0)}%")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Share Progress")
                        Switch(
                            checked = uiState.schoolLinking?.progressSharingEnabled ?: false,
                            onCheckedChange = onToggleSharing
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onShareProgress,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Share Now")
                        }
                        
                        OutlinedButton(
                            onClick = onUnlink,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Unlink")
                        }
                    }
                }
            }
        }
        
        // Module Schedule
        item {
            Text(
                text = "Module Schedule",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        items(uiState.moduleSchedules) { schedule ->
            ModuleScheduleItem(schedule)
        }
        
        // Progress Report
        uiState.latestReport?.let { report ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Latest Progress Report",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider()
                        
                        Text("Completed: ${report.completedModules}/${report.totalModules} modules")
                        Text("Average Score: ${String.format("%.1f", report.averageTestScore)}%")
                        Text("Study Time: ${report.totalStudyTime} minutes")
                        Text("Current Streak: ${report.currentStreak} days")
                        Text(
                            text = "Report Date: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(report.reportDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleScheduleItem(schedule: com.dereva.smart.domain.model.ModuleSchedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isUnlocked)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${schedule.order}. ${schedule.moduleName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (schedule.isUnlocked) "Unlocked" else "Unlocks: ${SimpleDateFormat("MMM dd", Locale.getDefault()).format(schedule.unlockDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = if (schedule.isUnlocked) Icons.Default.Check else Icons.Default.Lock,
                contentDescription = if (schedule.isUnlocked) "Unlocked" else "Locked",
                tint = if (schedule.isUnlocked)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LinkSchoolDialog(
    onDismiss: () -> Unit,
    onLink: (String) -> Unit
) {
    var schoolCode by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Link to School") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter your school code to link your account")
                OutlinedTextField(
                    value = schoolCode,
                    onValueChange = { schoolCode = it.uppercase() },
                    label = { Text("School Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onLink(schoolCode) },
                enabled = schoolCode.isNotBlank()
            ) {
                Text("Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

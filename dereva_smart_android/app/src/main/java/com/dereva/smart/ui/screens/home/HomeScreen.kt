package com.dereva.smart.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.ui.navigation.Screen
import com.dereva.smart.ui.screens.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dereva Smart")
                        if (currentUser != null && !currentUser.isGuestMode) {
                            Text(
                                text = "Hello, ${currentUser.name}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (currentUser?.isGuestMode == true) {
                        TextButton(
                            onClick = { navController.navigate(Screen.Login.route) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Login",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Login")
                        }
                    } else if (currentUser != null) {
                        IconButton(onClick = { authViewModel.logout() }) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Guest Mode Banner
            if (currentUser?.isGuestMode == true) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Free Trial Mode",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Category: ${currentUser.targetCategory.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            TextButton(
                                onClick = { navController.navigate(Screen.Login.route) }
                            ) {
                                Text("Login")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.CategorySelection.route) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Category")
                        }
                    }
                }
            }
            
            Text(
                text = "NTSA Driving Theory Test",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Master your driving theory with AI-powered learning",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Learning Modules Card
            FeatureCard(
                title = "Learning Modules",
                description = "Study road signs, rules, and regulations",
                icon = Icons.Default.Menu,
                onClick = { navController.navigate(Screen.ModuleList.route) }
            )
            
            // Mock Test Card
            FeatureCard(
                title = "Mock Tests",
                description = "Practice with 50-question tests",
                icon = Icons.Default.Edit,
                onClick = { navController.navigate(Screen.MockTest.route) }
            )
            
            // Progress Card
            FeatureCard(
                title = "Progress Tracking",
                description = "Monitor your learning journey",
                icon = Icons.Default.Star,
                onClick = { navController.navigate(Screen.Progress.route) }
            )
            
            // AI Tutor Card
            FeatureCard(
                title = "AI Tutor",
                description = "Get instant answers to your questions",
                icon = Icons.Default.Person,
                onClick = { navController.navigate(Screen.Tutor.route) }
            )
            
            // School Sync Card
            FeatureCard(
                title = "School Sync",
                description = "Link with your driving school",
                icon = Icons.Default.AccountCircle,
                onClick = { navController.navigate(Screen.School.route) }
            )
            
            // Payment Card
            FeatureCard(
                title = "Subscription",
                description = "Manage your subscription and payments",
                icon = Icons.Default.ShoppingCart,
                onClick = { navController.navigate(Screen.Payment.route) }
            )
            
            // 3D Simulation Card
            FeatureCard(
                title = "3D Driving Simulation",
                description = "Practice driving in a virtual town",
                icon = Icons.Default.Place,
                onClick = { navController.navigate(Screen.Simulation.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

package com.dereva.smart.ui.screens.home

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val authViewModel: AuthViewModel = koinViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Dereva Smart")
                        if (currentUser != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentUser.isGuestMode) "Guest Mode" else "Hello, ${currentUser.name}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (currentUser.isPremium) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = if (currentUser.isPremium) "PREMIUM" else "FREE",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        color = if (currentUser.isPremium) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Share App Button
                    IconButton(onClick = {
                        val referralCode = currentUser?.id?.take(8)?.uppercase() ?: "DEREVA"
                        val shareText = "Join me on Dereva Smart and ace your NTSA driving test! Use my referral code: $referralCode\n\nDownload: https://play.google.com/store/apps/details?id=com.dereva.smart"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share App",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    
                    if (currentUser == null || currentUser.isGuestMode) {
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
                    } else {
                        // Profile Button
                        IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val currentUserVal = currentUser // Create a stable val for smart casting
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reconciled Status Banner
            if (currentUserVal?.isPremium == false) {
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
                                    text = if (currentUserVal.isGuestMode) "Ready to start learning?" else "Upgrade to Premium",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Unlock all mock tests and 3D simulation.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentUserVal.isGuestMode) {
                                Button(
                                    onClick = { navController.navigate(Screen.Register.route) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Create Account")
                                }
                                OutlinedButton(
                                    onClick = { navController.navigate(Screen.CategorySelection.route) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Change Category")
                                }
                            } else {
                                // Show Get Premium button
                                Button(
                                    onClick = { navController.navigate(Screen.Payment.route) },
                                    modifier = if (currentUserVal.isSubscriptionActive) Modifier.fillMaxWidth() else Modifier.weight(1f)
                                ) {
                                    Text("Get Premium")
                                }
                                
                                // Show Change Category button only if subscription is NOT active
                                if (!currentUserVal.isSubscriptionActive) {
                                    OutlinedButton(
                                        onClick = { navController.navigate(Screen.CategorySelection.route) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Change Category")
                                    }
                                }
                            }
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
            
            // Help & Support Card - Prominent placement
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { navController.navigate(Screen.Help.route) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column {
                            Text(
                                text = "Need Help?",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "FAQs, contact us, and useful links",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                status = if (currentUser?.isPremium == true) "Active" else "Upgrade",
                onClick = { navController.navigate(Screen.Payment.route) }
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
    status: String? = null,
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
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    status?.let {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = it.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

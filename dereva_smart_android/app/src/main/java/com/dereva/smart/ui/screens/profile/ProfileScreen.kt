package com.dereva.smart.ui.screens.profile

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dereva.smart.ui.screens.auth.AuthViewModel
import com.dereva.smart.ui.screens.auth.SchoolSelectionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    val currentUser = uiState.currentUser
    var showSchoolDialog by remember { mutableStateOf(false) }
    
    // Load schools when screen opens
    LaunchedEffect(Unit) {
        authViewModel.loadSchools()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Info Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = currentUser?.name ?: "Guest",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            if (!currentUser?.isGuestMode!!) {
                                Text(
                                    text = currentUser.phoneNumber,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Surface(
                            color = if (currentUser?.isPremium == true) 
                                MaterialTheme.colorScheme.tertiaryContainer 
                            else 
                                MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (currentUser?.isPremium == true) "PREMIUM" else "FREE",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (currentUser?.isPremium == true) 
                                    MaterialTheme.colorScheme.onTertiaryContainer 
                                else 
                                    MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "License Category: ${currentUser?.targetCategory?.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Driving School Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Driving School",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val linkedSchool = uiState.schools.find { it.id == currentUser?.drivingSchoolId }
                    
                    if (linkedSchool != null) {
                        Text(linkedSchool.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            linkedSchool.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { showSchoolDialog = true }) {
                                Text("Change School")
                            }
                            OutlinedButton(onClick = { authViewModel.updateUserSchool(null) }) {
                                Text("Unlink")
                            }
                        }
                    } else {
                        Text(
                            "Not linked to any school",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(onClick = { showSchoolDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Select School")
                        }
                    }
                }
            }
            
            // Help Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = { navController.navigate("help") }
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
                                text = "Get support and learn how to use the app",
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
            
            // Referral Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Your Referral Code",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentUser?.id?.take(8)?.uppercase() ?: "DEREVA",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = {
                                val referralCode = currentUser?.id?.take(8)?.uppercase() ?: "DEREVA"
                                val shareText = "Join me on Dereva Smart and ace your NTSA driving test! Use my referral code: $referralCode\n\nDownload: https://play.google.com/store/apps/details?id=com.dereva.smart"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share via"))
                            }
                        ) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Share this code with friends and earn rewards!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Logout Button
            OutlinedButton(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
    
    // School Selection Dialog
    if (showSchoolDialog) {
        SchoolSelectionDialog(
            schools = uiState.schools,
            onSchoolSelected = { school ->
                school?.let { authViewModel.updateUserSchool(it.id) }
                showSchoolDialog = false
            },
            onDismiss = { showSchoolDialog = false }
        )
    }
}

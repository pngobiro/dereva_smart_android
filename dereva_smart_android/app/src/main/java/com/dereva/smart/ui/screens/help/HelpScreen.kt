package com.dereva.smart.ui.screens.help

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.dereva.smart.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current
    var expandedFaq by remember { mutableStateOf<Int?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contact Support Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contact Support",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Email
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:support@derevasmart.co.ke")
                                    putExtra(Intent.EXTRA_SUBJECT, "Dereva Smart Support Request")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Email", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "support@derevasmart.co.ke",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Divider()
                    
                    // Phone
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:+254712345678")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Phone", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "+254 712 345 678",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Divider()
                    
                    // WhatsApp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/254712345678?text=Hello, I need help with Dereva Smart")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "+254 712 345 678",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            
            // FAQs Section
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // FAQ Items
            val faqs = listOf(
                "How do I upgrade to Premium?" to "Tap on the 'Subscription' card on the home screen, select your payment method (M-Pesa or Card), and complete the payment. Your premium access will be activated immediately.",
                "What's included in Premium?" to "Premium includes unlimited access to all mock tests, 3D driving simulation, progress tracking, and the ability to link with your driving school for progress sharing.",
                "How do I link my driving school?" to "Go to your Profile, tap on 'Select School' under the Driving School section, search for your school, and select it. Your quiz progress will be automatically shared with your school.",
                "Can I change my license category?" to "Yes, but only if you don't have an active premium subscription. Go to your Profile and you'll see the option to change your category.",
                "How long does Premium last?" to "Premium subscription is valid for 30 days from the date of purchase. You'll receive a notification before it expires.",
                "What payment methods are accepted?" to "We accept M-Pesa and credit/debit cards. M-Pesa is the most popular and instant payment method.",
                "Can I get a refund?" to "Refunds are processed on a case-by-case basis. Please contact our support team with your payment details and reason for refund request.",
                "How do I share the app?" to "Tap the share icon in the top bar of the home screen. You'll get a unique referral code that your friends can use when they sign up.",
                "Is my progress saved?" to "Yes, all your progress is automatically saved to the cloud. You can access it from any device by logging in with your phone number.",
                "What if I forget my password?" to "On the login screen, tap 'Forgot Password'. Enter your phone number and you'll receive a verification code to reset your password."
            )
            
            faqs.forEachIndexed { index, (question, answer) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (expandedFaq == index) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedFaq = if (expandedFaq == index) null else index }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (expandedFaq == index) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                        
                        if (expandedFaq == index) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Useful Links
            Text(
                text = "Useful Links",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // NTSA Website
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://www.ntsa.go.ke")
                                }
                                context.startActivity(intent)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("NTSA Official Website")
                    }
                    
                    Divider()
                    
                    // Terms of Service
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO: Add terms of service link
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Terms of Service")
                    }
                    
                    Divider()
                    
                    // Privacy Policy
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO: Add privacy policy link
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Privacy Policy")
                    }
                }
            }
            
            // App Version
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = stringResource(R.string.app_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

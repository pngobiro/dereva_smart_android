package com.dereva.smart.ui.screens.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dereva.smart.domain.model.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.dereva.smart.ui.payment.MpesaPaymentActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val mpesaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val checkoutRequestId = if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("checkoutRequestID")
        } else null
        
        uiState.mpesaLaunchEvent?.paymentRequestId?.let { reqId -> 
            viewModel.handleMpesaResult(reqId, checkoutRequestId)
        }
    }

    LaunchedEffect(uiState.mpesaLaunchEvent) {
        uiState.mpesaLaunchEvent?.let { event ->
            val intent = Intent(context, MpesaPaymentActivity::class.java).apply {
                putExtra("phone", event.phoneNumber)
                putExtra("amount", event.amount.toDouble()) // doubleExtra reads double
            }
            mpesaLauncher.launch(intent)
            viewModel.onMpesaLaunchHandled()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription & Payment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active Subscription
            item {
                ActiveSubscriptionCard(
                    subscription = uiState.activeSubscription,
                    onToggleAutoRenew = { id, autoRenew ->
                        viewModel.toggleAutoRenew(id, autoRenew)
                    },
                    onCancel = { viewModel.cancelSubscription() }
                )
            }
            
            // Available Plans
            item {
                Text(
                    text = "Available Plans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(uiState.availablePlans) { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = uiState.selectedPlan == plan,
                    onSelect = { viewModel.selectPlan(plan) }
                )
            }
            
            // Payment Form
            if (uiState.selectedPlan != null) {
                item {
                    PaymentForm(
                        phoneNumber = uiState.phoneNumber,
                        promoCode = uiState.promoCode,
                        referralCode = uiState.referralCode,
                        finalAmount = uiState.finalAmount,
                        isProcessing = uiState.isProcessing,
                        onPhoneNumberChange = { viewModel.updatePhoneNumber(it) },
                        onPromoCodeChange = { viewModel.updatePromoCode(it) },
                        onReferralCodeChange = { viewModel.updateReferralCode(it) },
                        onSubmit = { viewModel.initiatePayment() }
                    )
                }
            }
            
            // Payment Status
            if (uiState.paymentStatus != null) {
                item {
                    PaymentStatusCard(
                        status = uiState.paymentStatus!!,
                        errorMessage = uiState.errorMessage,
                        onDismiss = { viewModel.resetPaymentStatus() }
                    )
                }
            }
            
            // Referral Code
            item {
                ReferralCodeCard(
                    referralCode = uiState.userReferralCode,
                    onGenerate = { viewModel.generateReferralCode() }
                )
            }
            
            // Payment History
            if (uiState.paymentHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "Payment History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(uiState.paymentHistory) { payment ->
                    PaymentHistoryItem(payment)
                }
            }
        }
    }
    
    // Error Snackbar
    if (uiState.errorMessage != null && uiState.paymentStatus == null) {
        LaunchedEffect(uiState.errorMessage) {
            viewModel.clearError()
        }
    }
}

@Composable
fun ActiveSubscriptionCard(
    subscription: UserSubscription?,
    onToggleAutoRenew: (String, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Current Subscription",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (subscription != null) {
                Text("Plan: ${subscription.tier.name}")
                Text("Status: ${if (subscription.isActive) "Active" else "Inactive"}")
                subscription.endDate?.let {
                    Text("Expires: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)}")
                    Text("Days Remaining: ${subscription.daysRemaining}")
                }
                
                if (subscription.tier == SubscriptionTier.MONTHLY) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-Renew")
                        Switch(
                            checked = subscription.autoRenew,
                            onCheckedChange = { onToggleAutoRenew(subscription.id, it) }
                        )
                    }
                }
                
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel Subscription")
                }
            } else {
                Text("No active subscription")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        onClick = onSelect
    ) {
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
                    text = plan.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${plan.currency} ${plan.price.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            plan.durationDays?.let {
                Text(
                    text = "Valid for $it days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            HorizontalDivider()
            
            plan.features.forEach { feature ->
                Text(
                    text = "• $feature",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PaymentForm(
    phoneNumber: String,
    promoCode: String,
    referralCode: String,
    finalAmount: Double?,
    isProcessing: Boolean,
    onPhoneNumberChange: (String) -> Unit,
    onPromoCodeChange: (String) -> Unit,
    onReferralCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Payment Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("M-Pesa Phone Number") },
                placeholder = { Text("0712345678") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )
            
            OutlinedTextField(
                value = promoCode,
                onValueChange = onPromoCodeChange,
                label = { Text("Promo Code (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )
            
            OutlinedTextField(
                value = referralCode,
                onValueChange = onReferralCodeChange,
                label = { Text("Referral Code (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )
            
            if (finalAmount != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Amount:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "KES ${finalAmount.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing && phoneNumber.isNotBlank()
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                } else {
                    Text("Pay with M-Pesa")
                }
            }
            
            Text(
                text = "You will receive an M-Pesa prompt on your phone",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PaymentStatusCard(
    status: PaymentStatus,
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                PaymentStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                PaymentStatus.FAILED, PaymentStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when (status) {
                    PaymentStatus.COMPLETED -> "✓ Payment Successful!"
                    PaymentStatus.FAILED -> "✗ Payment Failed"
                    PaymentStatus.CANCELLED -> "Payment Cancelled"
                    PaymentStatus.PROCESSING -> "Processing Payment..."
                    else -> "Payment Status: ${status.name}"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (status == PaymentStatus.COMPLETED) {
                Text("Your subscription has been activated successfully.")
            }
            
            errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            if (status != PaymentStatus.PROCESSING) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
fun ReferralCodeCard(
    referralCode: ReferralCode?,
    onGenerate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your Referral Code",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (referralCode != null) {
                Text(
                    text = referralCode.code,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Share this code and earn ${referralCode.referrerBonusPercentage}% bonus!")
                Text("Times used: ${referralCode.usageCount}")
            } else {
                Text("Generate your referral code to earn bonuses")
                Button(
                    onClick = onGenerate,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Referral Code")
                }
            }
        }
    }
}

@Composable
fun PaymentHistoryItem(payment: PaymentHistory) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = payment.subscriptionTier.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(payment.date),
                    style = MaterialTheme.typography.bodySmall
                )
                payment.mpesaReceiptNumber?.let {
                    Text(
                        text = "Receipt: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "KES ${payment.amount.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = payment.status.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (payment.status) {
                        PaymentStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                        PaymentStatus.FAILED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

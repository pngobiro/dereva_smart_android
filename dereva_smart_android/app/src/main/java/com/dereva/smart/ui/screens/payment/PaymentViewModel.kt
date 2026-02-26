package com.dereva.smart.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dereva.smart.domain.model.*
import com.dereva.smart.domain.repository.AuthRepository
import com.dereva.smart.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MpesaLaunchEvent(
    val paymentRequestId: String,
    val amount: Double,
    val phoneNumber: String
)

data class PaymentUiState(
    val availablePlans: List<SubscriptionPlan> = emptyList(),
    val selectedPlan: SubscriptionPlan? = null,
    val phoneNumber: String = "",
    val promoCode: String = "",
    val referralCode: String = "",
    val isProcessing: Boolean = false,
    val paymentStatus: PaymentStatus? = null,
    val errorMessage: String? = null,
    val activeSubscription: UserSubscription? = null,
    val paymentHistory: List<PaymentHistory> = emptyList(),
    val userReferralCode: ReferralCode? = null,
    val finalAmount: Double? = null,
    val mpesaLaunchEvent: MpesaLaunchEvent? = null
)

class PaymentViewModel(
    private val paymentRepository: PaymentRepository,
    private val authRepository: AuthRepository,
    private val userId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    init {
        checkPendingPayment()
        loadAvailablePlans()
        loadActiveSubscription()
        loadPaymentHistory()
        loadUserReferralCode()
    }
    
    private fun checkPendingPayment() {
        viewModelScope.launch {
            val (paymentRequestId, checkoutRequestId) = paymentRepository.getPendingPayment()
            if (paymentRequestId != null && checkoutRequestId != null) {
                _uiState.update { it.copy(isProcessing = true) }
                pollPaymentStatus(paymentRequestId, checkoutRequestId)
            }
        }
    }
    
    private fun loadAvailablePlans() {
        viewModelScope.launch {
            paymentRepository.getAvailablePlans().onSuccess { plans ->
                _uiState.update { 
                    it.copy(
                        availablePlans = plans,
                        selectedPlan = plans.firstOrNull(),
                        finalAmount = plans.firstOrNull()?.price
                    )
                }
                calculateFinalAmount()
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Failed to load plans") }
            }
        }
    }
    
    private fun loadActiveSubscription() {
        viewModelScope.launch {
            paymentRepository.getActiveSubscriptionFlow(userId)
                .collect { subscription ->
                    _uiState.update { it.copy(activeSubscription = subscription) }
                }
        }
    }
    
    private fun loadPaymentHistory() {
        viewModelScope.launch {
            paymentRepository.getPaymentHistory(userId)
                .onSuccess { history ->
                    _uiState.update { it.copy(paymentHistory = history) }
                }
        }
    }
    
    private fun loadUserReferralCode() {
        viewModelScope.launch {
            paymentRepository.getUserReferralCode(userId)
                .onSuccess { code ->
                    _uiState.update { it.copy(userReferralCode = code) }
                }
        }
    }
    
    fun selectPlan(plan: SubscriptionPlan) {
        _uiState.update { 
            it.copy(
                selectedPlan = plan,
                finalAmount = plan.price
            ) 
        }
        calculateFinalAmount()
    }
    
    fun updatePhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone) }
    }
    
    fun updatePromoCode(code: String) {
        _uiState.update { it.copy(promoCode = code) }
        calculateFinalAmount()
    }
    
    fun updateReferralCode(code: String) {
        _uiState.update { it.copy(referralCode = code) }
        calculateFinalAmount()
    }
    
    private fun calculateFinalAmount() {
        val state = _uiState.value
        val plan = state.selectedPlan ?: return
        
        viewModelScope.launch {
            var amount = plan.price
            
            if (state.promoCode.isNotBlank()) {
                paymentRepository.applyPromoCode(state.promoCode, amount)
                    .onSuccess { discountedAmount ->
                        amount = discountedAmount
                    }
            }
            
            if (state.referralCode.isNotBlank()) {
                paymentRepository.applyReferralCode(state.referralCode, amount)
                    .onSuccess { discountedAmount ->
                        amount = discountedAmount
                    }
            }
            
            _uiState.update { it.copy(finalAmount = amount) }
        }
    }
    
    fun initiatePayment() {
        val state = _uiState.value
        val plan = state.selectedPlan
        
        if (plan == null) {
            _uiState.update { it.copy(errorMessage = "Please select a plan") }
            return
        }
        
        if (state.phoneNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter phone number") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isProcessing = true,
                    errorMessage = null,
                    paymentStatus = PaymentStatus.PROCESSING
                ) 
            }
            
            paymentRepository.initiatePayment(
                userId = userId,
                phoneNumber = state.phoneNumber,
                plan = plan,
                promoCode = state.promoCode.takeIf { it.isNotBlank() },
                referralCode = state.referralCode.takeIf { it.isNotBlank() },
                schoolId = null
            ).onSuccess { paymentRequest ->
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        mpesaLaunchEvent = MpesaLaunchEvent(
                            paymentRequestId = paymentRequest.id,
                            amount = paymentRequest.amount,
                            phoneNumber = paymentRequest.phoneNumber
                        )
                    )
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        paymentStatus = PaymentStatus.FAILED,
                        errorMessage = error.message ?: "Payment initiation failed"
                    ) 
                }
            }
        }
    }
    
    fun onMpesaLaunchHandled() {
         _uiState.update { it.copy(mpesaLaunchEvent = null, isProcessing = true) }
    }

    fun handleMpesaResult(paymentRequestId: String, checkoutRequestId: String?) {
        if (checkoutRequestId != null) {
            viewModelScope.launch {
                paymentRepository.savePendingPayment(paymentRequestId, checkoutRequestId)
            }
            pollPaymentStatus(paymentRequestId, checkoutRequestId)
        } else {
             _uiState.update { it.copy(isProcessing = false, paymentStatus = PaymentStatus.FAILED, errorMessage = "Payment cancelled or failed") }
        }
    }
    
    private fun pollPaymentStatus(requestId: String, checkoutRequestId: String) {
        viewModelScope.launch {
            paymentRepository.pollPaymentStatus(
                requestId = requestId,
                checkoutRequestId = checkoutRequestId,
                maxAttempts = 24, // 2 minutes
                delayMillis = 5000
            ).onSuccess { result ->
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        paymentStatus = result.status,
                        errorMessage = result.errorMessage
                    ) 
                }
                
                if (result.status != PaymentStatus.PROCESSING) {
                    paymentRepository.clearPendingPayment()
                }
                
                if (result.status == PaymentStatus.COMPLETED) {
                    authRepository.refreshUser()
                    loadPaymentHistory()
                    loadActiveSubscription()
                }
            }.onFailure { error ->
                paymentRepository.clearPendingPayment()
                _uiState.update { 
                    it.copy(
                        isProcessing = false,
                        paymentStatus = PaymentStatus.FAILED,
                        errorMessage = error.message ?: "Payment verification failed"
                    ) 
                }
            }
        }
    }
    
    fun generateReferralCode() {
        viewModelScope.launch {
            paymentRepository.generateReferralCode(userId)
                .onSuccess { code ->
                    _uiState.update { it.copy(userReferralCode = code) }
                }
        }
    }
    
    fun toggleAutoRenew(subscriptionId: String, autoRenew: Boolean) {
        viewModelScope.launch {
            paymentRepository.updateAutoRenew(subscriptionId, autoRenew)
        }
    }
    
    fun cancelSubscription() {
        viewModelScope.launch {
            paymentRepository.cancelSubscription(userId)
                .onSuccess {
                    loadActiveSubscription()
                }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun resetPaymentStatus() {
        _uiState.update { 
            it.copy(
                paymentStatus = null,
                errorMessage = null,
                phoneNumber = "",
                promoCode = "",
                referralCode = "",
                selectedPlan = null,
                finalAmount = null
            ) 
        }
    }
}

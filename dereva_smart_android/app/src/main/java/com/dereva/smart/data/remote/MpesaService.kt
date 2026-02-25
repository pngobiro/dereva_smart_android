package com.dereva.smart.data.remote

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class MpesaConfig(
    val serverUrl: String,
    val callbackUrl: String,
    val productCode: String,
    val passkey: String,
    val consumerKey: String,
    val consumerSecret: String,
    val shortCode: String
)

data class StkPushResponse(
    val success: Boolean,
    val checkoutRequestId: String?,
    val merchantRequestId: String?,
    val responseCode: String?,
    val responseDescription: String?,
    val customerMessage: String?
)

data class PaymentStatusResponse(
    val success: Boolean,
    val resultCode: String?,
    val resultDesc: String?,
    val mpesaReceiptNumber: String?,
    val transactionDate: String?
)

class MpesaService(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    
    private val config: MpesaConfig by lazy {
        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val metaData = appInfo.metaData
        
        MpesaConfig(
            serverUrl = metaData.getString("com.birosoft.payments.SERVER_URL_API") 
                ?: throw IllegalStateException("SERVER_URL_API not configured"),
            callbackUrl = metaData.getString("com.birosoft.payments.CALLBACK_URL")
                ?: throw IllegalStateException("CALLBACK_URL not configured"),
            productCode = metaData.get("com.birosoft.payments.PRODUCT_CODE")?.toString()
                ?: throw IllegalStateException("PRODUCT_CODE not configured"),
            passkey = metaData.getString("com.birosoft.payments.MPESA_PASSKEY")
                ?: throw IllegalStateException("MPESA_PASSKEY not configured"),
            consumerKey = metaData.getString("com.birosoft.payments.MPESA_CONSUMER_KEY")
                ?: throw IllegalStateException("MPESA_CONSUMER_KEY not configured"),
            consumerSecret = metaData.getString("com.birosoft.payments.MPESA_CONSUMER_SECRET")
                ?: throw IllegalStateException("MPESA_CONSUMER_SECRET not configured"),
            shortCode = metaData.get("com.birosoft.payments.MPESA_SHORT_CODE")?.toString()
                ?: throw IllegalStateException("MPESA_SHORT_CODE not configured")
        )
    }
    
    suspend fun initiateStkPush(
        phoneNumber: String,
        amount: Double,
        accountReference: String,
        transactionDesc: String
    ): Result<StkPushResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
            val password = generatePassword(timestamp)
            
            val json = JSONObject().apply {
                put("BusinessShortCode", config.shortCode)
                put("Password", password)
                put("Timestamp", timestamp)
                put("TransactionType", "CustomerPayBillOnline")
                put("Amount", amount.toInt())
                put("PartyA", formatPhoneNumber(phoneNumber))
                put("PartyB", config.shortCode)
                put("PhoneNumber", formatPhoneNumber(phoneNumber))
                put("CallBackURL", config.callbackUrl)
                put("AccountReference", accountReference)
                put("TransactionDesc", transactionDesc)
            }
            
            val requestBody = json.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${getAccessToken()}")
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful) {
                parseStkPushResponse(responseBody)
            } else {
                StkPushResponse(
                    success = false,
                    checkoutRequestId = null,
                    merchantRequestId = null,
                    responseCode = response.code.toString(),
                    responseDescription = "Request failed: ${response.message}",
                    customerMessage = "Payment request failed. Please try again."
                )
            }
        }
    }
    
    suspend fun checkPaymentStatus(checkoutRequestId: String): Result<PaymentStatusResponse> = 
        withContext(Dispatchers.IO) {
            runCatching {
                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = generatePassword(timestamp)
                
                val json = JSONObject().apply {
                    put("BusinessShortCode", config.shortCode)
                    put("Password", password)
                    put("Timestamp", timestamp)
                    put("CheckoutRequestID", checkoutRequestId)
                }
                
                val requestBody = json.toString().toRequestBody("application/json".toMediaType())
                
                val request = Request.Builder()
                    .url("https://api.safaricom.co.ke/mpesa/stkpushquery/v1/query")
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer ${getAccessToken()}")
                    .build()
                
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                
                if (response.isSuccessful) {
                    parsePaymentStatusResponse(responseBody)
                } else if (response.code == 400 || response.code == 500) {
                    // Safaricom sends 400 or 500 when transaction is still processing or cancelled.
                    // We must parse it to see what happened.
                    PaymentStatusResponse(
                        success = false,
                        resultCode = JSONObject(responseBody).optString("errorCode", "-1"),
                        resultDesc = JSONObject(responseBody).optString("errorMessage", "Failed to check status: ${response.code}"),
                        mpesaReceiptNumber = null,
                        transactionDate = null
                    )
                } else {
                    PaymentStatusResponse(
                        success = false,
                        resultCode = null,
                        resultDesc = "Failed to check status: ${response.code}",
                        mpesaReceiptNumber = null,
                        transactionDate = null
                    )
                }
            }
        }
    
    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        // In production, this should be cached and refreshed when expired
        val credentials = "${config.consumerKey}:${config.consumerSecret}"
        val encodedCredentials = android.util.Base64.encodeToString(
            credentials.toByteArray(),
            android.util.Base64.NO_WRAP
        )
        
        val request = Request.Builder()
            .url("https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .get()
            .addHeader("Authorization", "Basic $encodedCredentials")
            .build()
        
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        
        if (response.isSuccessful) {
            val json = JSONObject(responseBody)
            json.optString("access_token", "")
        } else {
            throw IOException("Failed to get access token: ${response.code} $responseBody | URLs: ${request.url} Auth: Basic $encodedCredentials")
        }
    }
    
    private fun generatePassword(timestamp: String): String {
        val rawPassword = "${config.shortCode}${config.passkey}$timestamp"
        return android.util.Base64.encodeToString(
            rawPassword.toByteArray(),
            android.util.Base64.NO_WRAP
        )
    }
    
    private fun formatPhoneNumber(phone: String): String {
        var formatted = phone.replace(Regex("[^0-9]"), "")
        
        // Convert to 254 format
        when {
            formatted.startsWith("254") -> return formatted
            formatted.startsWith("0") -> return "254${formatted.substring(1)}"
            formatted.startsWith("+254") -> return formatted.substring(1)
            formatted.length == 9 -> return "254$formatted"
            else -> return formatted
        }
    }
    
    private fun parseStkPushResponse(json: String): StkPushResponse {
        val jsonObject = JSONObject(json)
        return StkPushResponse(
            success = jsonObject.optString("ResponseCode") == "0",
            checkoutRequestId = jsonObject.optString("CheckoutRequestID"),
            merchantRequestId = jsonObject.optString("MerchantRequestID"),
            responseCode = jsonObject.optString("ResponseCode"),
            responseDescription = jsonObject.optString("ResponseDescription"),
            customerMessage = jsonObject.optString("CustomerMessage")
        )
    }
    
    private fun parsePaymentStatusResponse(json: String): PaymentStatusResponse {
        val jsonObject = JSONObject(json)
        return PaymentStatusResponse(
            success = jsonObject.optString("ResultCode") == "0",
            resultCode = jsonObject.optString("ResultCode"),
            resultDesc = jsonObject.optString("ResultDesc"),
            mpesaReceiptNumber = jsonObject.optString("MpesaReceiptNumber"),
            transactionDate = jsonObject.optString("TransactionDate")
        )
    }
}

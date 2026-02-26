package com.dereva.smart.ui.payment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bdhobare.mpesa.Mode
import com.bdhobare.mpesa.Mpesa
import com.bdhobare.mpesa.interfaces.AuthListener
import com.bdhobare.mpesa.interfaces.MpesaListener
import com.bdhobare.mpesa.models.STKPush
import com.bdhobare.mpesa.utils.Pair

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.dereva.smart.R

class MpesaPaymentActivity : AppCompatActivity(), MpesaListener, AuthListener {

    private var stkPush: STKPush? = null
    private lateinit var btnPay: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvPhone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpesa_payment)
        
        btnPay = findViewById(R.id.btnPay)
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)
        tvAmount = findViewById(R.id.tvAmount)
        tvPhone = findViewById(R.id.tvPhone)

        val amount = intent.getDoubleExtra("amount", 0.0).toInt()
        val phone = intent.getStringExtra("phone") ?: ""

        if (amount == 0 || phone.isEmpty()) {
            Toast.makeText(this, "Invalid payment details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvAmount.text = "KES $amount"
        // Ensure phone number is in 254... format for display
        val formattedPhone = if (phone.startsWith("0")) "254${phone.substring(1)}" else if (!phone.startsWith("254")) "254$phone" else phone
        tvPhone.text = formattedPhone

        val app = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val bundle = app.metaData
        val consumerKey = bundle.getString("com.birosoft.payments.MPESA_CONSUMER_KEY") ?: ""
        val consumerSecret = bundle.getString("com.birosoft.payments.MPESA_CONSUMER_SECRET") ?: ""
        
        // Use getInt().toString() like the working app if possible, or fallback to get().toString()
        val shortCode = bundle.get("com.birosoft.payments.MPESA_SHORT_CODE")?.toString() ?: ""
        val passkey = bundle.getString("com.birosoft.payments.MPESA_PASSKEY") ?: ""
        val callbackUrl = bundle.getString("com.birosoft.payments.CALLBACK_URL") ?: ""

        android.util.Log.d("MpesaPayment", "Preparing payment for $formattedPhone, amount $amount")

        stkPush = STKPush.Builder(
            shortCode,
            passkey,
            amount,
            shortCode,
            formattedPhone
        ).apply {
            setCallBackURL(callbackUrl)
        }.build()

        // Initialize SDK
        Mpesa.with(this, consumerKey, consumerSecret, Mode.PRODUCTION)

        btnPay.setOnClickListener {
            startPaymentProcess()
        }
    }

    private fun startPaymentProcess() {
        btnPay.isEnabled = false
        progressBar.visibility = View.VISIBLE
        tvStatus.text = "Contacting Safaricom..."
        
        stkPush?.let {
            Mpesa.getInstance().pay(this, it)
        } ?: run {
            Toast.makeText(this, "Internal error: Push object null", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onAuthError(result: Pair<Int, String>?) {
        val errorMsg = result?.toString() ?: "Authentication failed"
        android.util.Log.e("MpesaPayment", "Auth Error: $errorMsg")
        tvStatus.text = "Error: $errorMsg"
        progressBar.visibility = View.GONE
        btnPay.isEnabled = true
        Toast.makeText(this, "M-Pesa authentication failed", Toast.LENGTH_LONG).show()
    }

    override fun onAuthSuccess() {
        android.util.Log.d("MpesaPayment", "Auth Success")
        tvStatus.text = "Authenticated! Ready to pay."
        // We don't auto-trigger pay() here to avoid race conditions 
        // that lead to Invalid Access Token error. 
        // The user triggers it via btnPay.
    }

    override fun onMpesaError(result: Pair<Int, String>?) {
        val errorMsg = result?.toString() ?: "Payment request failed"
        android.util.Log.e("MpesaPayment", "Mpesa Error: $errorMsg")
        tvStatus.text = "Error: $errorMsg"
        progressBar.visibility = View.GONE
        btnPay.isEnabled = true
        Toast.makeText(this, "M-Pesa request failed: $errorMsg", Toast.LENGTH_LONG).show()
    }

    override fun onMpesaSuccess(
        merchantRequestID: String?,
        checkoutRequestID: String?,
        customerMessage: String?
    ) {
        android.util.Log.d("MpesaPayment", "Mpesa Success: $checkoutRequestID")
        Toast.makeText(this, customerMessage ?: "Payment request sent. Please check your phone.", Toast.LENGTH_LONG).show()
        val intent = Intent().apply {
            putExtra("checkoutRequestID", checkoutRequestID)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}

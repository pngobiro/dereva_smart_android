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

class MpesaPaymentActivity : AppCompatActivity(), MpesaListener, AuthListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val amount = intent.getDoubleExtra("amount", 0.0).toInt()
        val phone = intent.getStringExtra("phone") ?: ""

        if (amount == 0 || phone.isEmpty()) {
            Toast.makeText(this, "Invalid payment details", Toast.LENGTH_SHORT).show()
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        val app = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val bundle = app.metaData
        val consumerKey = bundle.getString("com.birosoft.payments.MPESA_CONSUMER_KEY") ?: ""
        val consumerSecret = bundle.getString("com.birosoft.payments.MPESA_CONSUMER_SECRET") ?: ""
        val shortCodeRaw = bundle.get("com.birosoft.payments.MPESA_SHORT_CODE")
        val shortCode = shortCodeRaw?.toString() ?: ""
        val passkey = bundle.getString("com.birosoft.payments.MPESA_PASSKEY") ?: ""
        val callbackUrl = bundle.getString("com.birosoft.payments.CALLBACK_URL") ?: ""

        Mpesa.with(this, consumerKey, consumerSecret, Mode.PRODUCTION)
        
        val push = STKPush.Builder(
            shortCode,
            passkey,
            amount,
            shortCode,
            phone
        ).apply {
            setCallBackURL(callbackUrl)
        }.build()

        Mpesa.getInstance().pay(this, push)
    }

    override fun onAuthError(result: Pair<Int, String>?) {
        Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_LONG).show()
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onAuthSuccess() {
        // success
    }

    override fun onMpesaError(result: Pair<Int, String>?) {
        Toast.makeText(this, "Payment error. Please try again.", Toast.LENGTH_LONG).show()
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onMpesaSuccess(
        merchantRequestID: String?,
        checkoutRequestID: String?,
        customerMessage: String?
    ) {
        Toast.makeText(this, customerMessage ?: "Payment request sent. Please check your phone.", Toast.LENGTH_LONG).show()
        val intent = Intent().apply {
            putExtra("checkoutRequestID", checkoutRequestID)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}

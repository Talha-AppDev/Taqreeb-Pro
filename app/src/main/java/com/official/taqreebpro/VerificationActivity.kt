package com.official.taqreebpro

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.official.taqreebpro.databinding.ActivityVerificationBinding
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var company: String
    private lateinit var city: String
    private lateinit var address: String
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the data passed from SignUpActivity
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        email = intent.getStringExtra("email")!!
        password = intent.getStringExtra("password")!!
        company = intent.getStringExtra("company")!!
        city = intent.getStringExtra("city")!!
        address = intent.getStringExtra("address")!!
        category = intent.getStringExtra("category")!!

        binding.progressBar.visibility = View.INVISIBLE

        addTextChangeListener()
        resendOTPTvVisibility()

        binding.btnProceed.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE

            val typedOTP = (binding.etOtp1.text.toString() + binding.etOtp2.text.toString() + binding.etOtp3.text.toString() +
                    binding.etOtp4.text.toString() + binding.etOtp5.text.toString() + binding.etOtp6.text.toString())
            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP, typedOTP
                    )
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(this, "Please Enter the correct OTP", Toast.LENGTH_LONG).show()
                }
            } else if (typedOTP.isEmpty()) {
                Toast.makeText(this, "Please Enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendOTPTvVisibility() {
        binding.etOtp1.setText("")
        binding.etOtp2.setText("")
        binding.etOtp3.setText("")
        binding.etOtp4.setText("")
        binding.etOtp5.setText("")
        binding.etOtp6.setText("")
    }

    private fun addTextChangeListener() {
        binding.etOtp1.addTextChangedListener(EditTextWatcher(binding.etOtp1))
        binding.etOtp2.addTextChangedListener(EditTextWatcher(binding.etOtp2))
        binding.etOtp3.addTextChangedListener(EditTextWatcher(binding.etOtp3))
        binding.etOtp4.addTextChangedListener(EditTextWatcher(binding.etOtp4))
        binding.etOtp5.addTextChangedListener(EditTextWatcher(binding.etOtp5))
        binding.etOtp6.addTextChangedListener(EditTextWatcher(binding.etOtp6))
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.etOtp1 -> if (text.length == 1) binding.etOtp2.requestFocus()
                R.id.etOtp2 -> if (text.length == 1) binding.etOtp3.requestFocus() else if (text.isEmpty()) binding.etOtp1.requestFocus()
                R.id.etOtp3 -> if (text.length == 1) binding.etOtp4.requestFocus() else if (text.isEmpty()) binding.etOtp2.requestFocus()
                R.id.etOtp4 -> if (text.length == 1) binding.etOtp5.requestFocus() else if (text.isEmpty()) binding.etOtp3.requestFocus()
                R.id.etOtp5 -> if (text.length == 1) binding.etOtp6.requestFocus() else if (text.isEmpty()) binding.etOtp4.requestFocus()
                R.id.etOtp6 -> if (text.isEmpty()) binding.etOtp5.requestFocus()
            }
            if (text.length > 1) {
                p0?.delete(1, text.length)
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    binding.progressBar.visibility = View.VISIBLE
                    Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_LONG).show()
                    storeUserData()
                } else {
                    Log.d(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid OTP, please try again", Toast.LENGTH_SHORT).show()
                    }
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
    }

    private fun storeUserData() {
        val user = hashMapOf(
            "email" to email,
            "phone" to phoneNumber,
            "password" to password,
            "company" to company,
            "city" to city,
            "address" to address,
            "category" to category
        )
    if(category=="Marquee") {
        db.collection("marquees").document(company)
            .set(user)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                sendToMain()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
                Toast.makeText(this, "Error saving data, please try again", Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.INVISIBLE
            }
    }else if(category=="Photographer"){
        db.collection("photographers").document(company)
            .set(user)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                sendToMain()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error writing document", e)
                Toast.makeText(this, "Error saving data, please try again", Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.INVISIBLE
            }
    }
    }

    private fun sendToMain() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("company", company)
        startActivity(intent)
        finish()
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(ContentValues.TAG, "onVerificationFailed: {$e.toString()}")
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d(ContentValues.TAG, "onCodeSent:$verificationId")
            OTP = verificationId
            resendToken = token
        }
    }

    override fun onStart() {
        binding.progressBar.visibility = View.INVISIBLE
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onStart()
    }
}

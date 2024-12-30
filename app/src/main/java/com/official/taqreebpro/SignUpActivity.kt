package com.official.taqreebpro

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.official.taqreebpro.databinding.ActivitySignUpBinding
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var number: String
    private var isPasswordVisible = false
    private var isPasswordVisible2 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.progressBar.visibility = View.INVISIBLE

        binding.btnSignUp.setOnClickListener {
            number = binding.etPhone.text.trim().toString()
            if (number.isNotEmpty()) {
                if (number.length == 10) {
                    number = "+92$number"
                    binding.progressBar.visibility = View.VISIBLE
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(this, "Please Enter Correct number without 0", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please Enter the Number", Toast.LENGTH_LONG).show()
            }
        }
        binding.ivHide.setOnClickListener {
            if (isPasswordVisible) {
                // Hide password
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                // Show password
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }

            // Move the cursor to the end of the text
            binding.etPassword.setSelection(binding.etPassword.text.length)

            isPasswordVisible = !isPasswordVisible
        }
        binding.ivHide2.setOnClickListener {
            if (isPasswordVisible2) {
                // Hide password
                binding.etPassword2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                // Show password
                binding.etPassword2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }

            // Move the cursor to the end of the text
            binding.etPassword2.setSelection(binding.etPassword2.text.length)

            isPasswordVisible2 = !isPasswordVisible2
        }
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // For older Android versions, use the older method
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(ContentValues.TAG, "onVerificationFailed: {$e.toString()}")

            binding.progressBar.visibility = View.INVISIBLE

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.d(ContentValues.TAG, "Invalid request: {$e.toString()}")
                Toast.makeText(this@SignUpActivity, "Invalid phone number.", Toast.LENGTH_LONG).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Log.d(ContentValues.TAG, "Quota exceeded: {$e.toString()}")
                Toast.makeText(this@SignUpActivity, "Quota exceeded, please try again later.", Toast.LENGTH_LONG).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                Log.d(ContentValues.TAG, "Missing activity: {$e.toString()}")
                Toast.makeText(this@SignUpActivity, "Missing recaptcha verification.", Toast.LENGTH_LONG).show()
            } else {
                Log.d(ContentValues.TAG, "Verification failed: {$e.toString()}")
                Toast.makeText(this@SignUpActivity, "Verification failed, please try again.", Toast.LENGTH_LONG).show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d(ContentValues.TAG, "onCodeSent:$verificationId")
            val intent = Intent(this@SignUpActivity, VerificationActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("resendToken", token)
            intent.putExtra("phoneNumber", number)

            // Pass user data to VerificationActivity
            intent.putExtra("email", binding.etEmail.text.toString().trim())
            intent.putExtra("phone", binding.etPhone.text.toString().trim())
            intent.putExtra("password", binding.etPassword.text.toString().trim())
            intent.putExtra("company", binding.etCompany.text.toString().trim())
            intent.putExtra("city", binding.srCity.selectedItem.toString())
            intent.putExtra("address", binding.etAddress.text.toString().trim())
            intent.putExtra("category", binding.srCategory.selectedItem.toString())

            binding.progressBar.visibility = View.INVISIBLE
            startActivity(intent)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    Toast.makeText(this, "Authenticated Successfully", Toast.LENGTH_LONG).show()
                } else {
                    Log.d(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    binding.progressBar.visibility = View.INVISIBLE
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Invalid verification code.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun sendToMain() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish() // Close the SignUpActivity
    }


    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidName(name: String): Boolean {
        val nameRegex = "^[\\p{L}\\s'.-]{3,30}$"
        return name.matches(nameRegex.toRegex())
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            // Show password
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }

        // Move the cursor to the end of the text
        binding.etPassword.setSelection(binding.etPassword.text.length)

        isPasswordVisible = !isPasswordVisible
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}

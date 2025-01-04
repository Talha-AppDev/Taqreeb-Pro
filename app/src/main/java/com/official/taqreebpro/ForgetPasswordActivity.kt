package com.official.taqreebpro

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.official.taqreebpro.databinding.ActivityForgetPasswordBinding
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendCode.setOnClickListener {
            val email = binding.etForgetEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordToEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordToEmail(email: String) {
        firestore.collection("marquees")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val password = document.getString("password")
                    val marqueeName = document.id // Marquee name is the document ID

                    if (password != null) {
                        sendAutomatedEmail(email, marqueeName, password)
                    } else {
                        Toast.makeText(this, "Password not found for this email", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to connect to database", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendAutomatedEmail(email: String, marqueeName: String, password: String) {
        val senderEmail = "taqreebofficial@gmail.com" // Replace with your email
        val senderPassword = "khhr tgol zrwg jloo" // Replace with your app password
        var subject = "Forgot Password? Here’s Your Taqreeb Access 🔑"
        val message = "Hey there, $marqueeName! \uD83C\uDF89\n" +
                "\n" +
                "Oops! Looks like you forgot your password—but don’t worry, superheroes forget their capes sometimes too! \uD83E\uDDB8\u200D♂\uFE0F\uD83E\uDDB8\u200D♀\uFE0F \n" +
                "\n" +
                "Here’s your password: **$password**  \n" +
                "\n" +
                "We suggest keeping it somewhere safe (or maybe writing it down on something cooler than a sticky note). \uD83D\uDCDD\n" +
                "\n" +
                "If this wasn’t you, don’t sweat it—just reach out, and we’ll save the day!  \n" +
                "\n" +
                "Stay awesome,  \n" +
                "The Taqreeb Squad \uD83D\uDE80  \n"

        // Configure properties for SMTP
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        // Create a session with the email credentials
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        // Send the email in a background thread
        Thread {
            try {
                val mimeMessage = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    addRecipient(Message.RecipientType.TO, InternetAddress(email))
                    setSubject(subject)
                    setText(message)
                }

                Transport.send(mimeMessage)
                runOnUiThread {
                    Toast.makeText(this@ForgetPasswordActivity, "Password sent to your email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@ForgetPasswordActivity, "Failed to send email: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onStart() {
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // For older Android versions, use the older method
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        super.onStart()
    }
}

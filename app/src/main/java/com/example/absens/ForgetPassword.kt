package com.example.absens

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.absens.databinding.ActivityForgetPasswordBinding
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()


        binding.btnSendEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                // Karena pakai TextInputLayout, error ditampilkan di tiEmail
                binding.tiEmail.error = "Masukkan email anda terlebih dahulu"
                binding.tiEmail.requestFocus()
            } else {
                binding.tiEmail.error = null // Clear error jika ada
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showSuccessVisual("Email reset berhasil dikirim ke $email")
                        } else {
                            showErrorVisual("Gagal mengirim email reset: ${task.exception?.message}")
                        }
                    }
            }
        }

        binding.tvBackLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showSuccessVisual(message: String) {
        val snackbar = Snackbar.make(binding.main, message, 3000)
        snackbar.setBackgroundTint(Color.parseColor("#4CAF50"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
        binding.main.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(500)
            .start()
    }

    private fun showErrorVisual(message: String) {
        val snackbar = Snackbar.make(binding.main, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.RED)
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}

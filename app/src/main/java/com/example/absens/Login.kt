package com.example.absens

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.absens.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // OPTIMASI: Aktifkan Cache Offline agar Firestore lebih cepat
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        firestore.firestoreSettings = settings

        // Setup UI
        setupWindowInsets()
        setupInputValidation()
        loadRememberMe()
        setupClickListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupInputValidation() {
        binding.etEmail.addTextChangedListener { binding.tiEmail.error = null }
        binding.etPassword.addTextChangedListener { binding.tiPassword.error = null }
    }

    private fun loadRememberMe() {
        val pref = getSharedPreferences("ABSENSIPref", MODE_PRIVATE)
        val savedEmail = pref.getString("remember_email", null)
        if (!savedEmail.isNullOrEmpty()) {
            binding.etEmail.setText(savedEmail)
            binding.cbrememberme.isChecked = true
        }
    }

    private fun setupClickListeners() {
        val pref = getSharedPreferences("ABSENSIPref", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim().lowercase()
            val password = binding.etPassword.text.toString().trim()

            // Validasi lokal (Sangat cepat)
            if (email.isEmpty()) {
                binding.tiEmail.error = "Email harus diisi"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tiPassword.error = "Password harus diisi"
                return@setOnClickListener
            }

            // LOGIN FIREBASE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Save Remember Me
                        val editor = pref.edit()
                        if (binding.cbrememberme.isChecked) {
                            editor.putString("remember_email", email)
                        } else {
                            editor.remove("remember_email")
                        }
                        editor.apply()

                        // UPDATE FIRESTORE (Background - Tidak ditunggu hasilnya)
                        user?.let { syncUserToFirestore(it) }

                        // LANGSUNG PINDAH DASHBOARD (Ini yang bikin terasa cepat)
                        startActivity(Intent(this, Dashboard::class.java))
                        finish()
                    } else {
                        // KEMBALIKAN UI JIKA GAGAL
                        binding.btnLogin.text = "LOGIN"
                        binding.btnLogin.isEnabled = true

                        val errorMsg = when {
                            task.exception?.message?.contains("password", true) == true -> "Password salah"
                            task.exception?.message?.contains("user", true) == true -> "User tidak ditemukan"
                            else -> "Login gagal, cek koneksi Anda"
                        }
                        showError(errorMsg)
                    }
                }
        }

        binding.tvSignUpLink.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }

    private fun syncUserToFirestore(user: com.google.firebase.auth.FirebaseUser) {
        // Cek/Update data user di background tanpa menghambat UI
        firestore.collection("user").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    val userMap = hashMapOf(
                        "email" to user.email,
                        "username" to "User"
                    )
                    firestore.collection("user").document(user.uid).set(userMap)
                }
            }
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.main, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.RED)
            .setTextColor(Color.WHITE)
            .show()
    }
}
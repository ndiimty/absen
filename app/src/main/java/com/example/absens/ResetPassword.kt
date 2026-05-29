package com.example.absens

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnReset = findViewById<Button>(R.id.btnReset)

        btnReset.setOnClickListener {

            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // VALIDASI
            if (newPassword.isEmpty()) {
                etNewPassword.error = "Password tidak boleh kosong"
                etNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                etNewPassword.error = "Password minimal 6 karakter"
                etNewPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                etConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                etConfirmPassword.error = "Password tidak sama"
                etConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            // SIMULASI BERHASIL
            Toast.makeText(
                this,
                "Password berhasil direset",
                Toast.LENGTH_SHORT
            ).show()

            // kembali ke login
            finish()
        }
    }
}
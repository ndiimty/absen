package com.example.absens

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.*

class CutiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti)

        // ================= NAVBAR =================
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navCalendar = findViewById<ImageView>(R.id.navCalendar)
        val navCamera = findViewById<ImageView>(R.id.navCamera)
        val navReport = findViewById<ImageView>(R.id.navReport)
        val navProfile = findViewById<ImageView>(R.id.navProfile)

        navHome.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        navCalendar.setOnClickListener {
            startActivity(Intent(this, KalenderActivity::class.java))
        }

        navCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        navReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // ================= HEADER =================
        val header = findViewById<android.view.View>(R.id.headerLayout)
        val txtTitle = header.findViewById<TextView>(R.id.txtTitle)
        txtTitle.text = "PENGAJUAN CUTI"

        // ================= FORM =================
        val etMulai = findViewById<EditText>(R.id.etMulai)
        val etSelesai = findViewById<EditText>(R.id.etSelesai)
        val etAlasan = findViewById<EditText>(R.id.etAlasan)
        val txtDurasi = findViewById<TextView>(R.id.txtDurasi)

        // 🔥 FIX: harus MaterialCardView
        val btnSubmit = findViewById<MaterialCardView>(R.id.btnSubmit)

        // jenis cuti
        val btnCutiTahunan = findViewById<MaterialCardView>(R.id.btnCutiTahunan)
        val btnCutiLainnya = findViewById<MaterialCardView>(R.id.btnCutiLainnya)

        val calendar = Calendar.getInstance()

        // ================= PARSE DATE =================
        fun parseDate(text: String): Calendar? {
            return try {
                val parts = text.split("/")
                if (parts.size != 3) return null

                val cal = Calendar.getInstance()
                cal.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())

                // reset jam biar akurat
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                cal
            } catch (e: Exception) {
                null
            }
        }

        // ================= HITUNG DURASI =================
        fun hitungDurasi() {
            val startDate = parseDate(etMulai.text.toString())
            val endDate = parseDate(etSelesai.text.toString())

            if (startDate != null && endDate != null) {
                val diff = endDate.timeInMillis - startDate.timeInMillis
                val days = (diff / (1000 * 60 * 60 * 24)) + 1

                if (days > 0) {
                    txtDurasi.text = "$days hari"
                } else {
                    txtDurasi.text = "— hari"
                }
            } else {
                txtDurasi.text = "— hari"
            }
        }

        // ================= DATE PICKER =================
        fun showDate(edit: EditText) {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    edit.setText("$d/${m + 1}/$y")
                    hitungDurasi()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etMulai.setOnClickListener { showDate(etMulai) }
        etSelesai.setOnClickListener { showDate(etSelesai) }

        // ================= PILIH JENIS CUTI =================
        fun resetCuti() {
            btnCutiTahunan.strokeWidth = 0
            btnCutiLainnya.strokeWidth = 0
        }

        btnCutiTahunan.setOnClickListener {
            resetCuti()
            btnCutiTahunan.strokeWidth = 3
        }

        btnCutiLainnya.setOnClickListener {
            resetCuti()
            btnCutiLainnya.strokeWidth = 3
        }

        // ================= SUBMIT =================
        btnSubmit.setOnClickListener {
            if (etMulai.text.isEmpty() || etSelesai.text.isEmpty() || etAlasan.text.isEmpty()) {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Cuti diajukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
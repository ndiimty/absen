package com.example.absens

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.util.*

class IzinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin)

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
        txtTitle.text = "PENGAJUAN IZIN"

        // ================= FORM =================
        val etTanggal = findViewById<EditText>(R.id.etTanggal)
        val etKeterangan = findViewById<EditText>(R.id.etKeterangan)

        // 🔥 FIX DI SINI (MaterialCardView, bukan Button)
        val btnKirim = findViewById<MaterialCardView>(R.id.btnKirim)

        // (optional) pilihan jenis izin
        val btnIzinPribadi = findViewById<MaterialCardView>(R.id.btnIzinPribadi)
        val btnIzinKeluarga = findViewById<MaterialCardView>(R.id.btnIzinKeluarga)
        val btnIzinDinas = findViewById<MaterialCardView>(R.id.btnIzinDinas)
        val btnIzinLainnya = findViewById<MaterialCardView>(R.id.btnIzinLainnya)

        val calendar = Calendar.getInstance()

        // ================= DATE PICKER =================
        etTanggal.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    etTanggal.setText("$d/${m + 1}/$y")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // ================= PILIH JENIS IZIN =================
        fun resetSelection() {
            btnIzinPribadi.strokeWidth = 0
            btnIzinKeluarga.strokeWidth = 0
            btnIzinDinas.strokeWidth = 0
            btnIzinLainnya.strokeWidth = 0
        }

        btnIzinPribadi.setOnClickListener {
            resetSelection()
            btnIzinPribadi.strokeWidth = 3
        }

        btnIzinKeluarga.setOnClickListener {
            resetSelection()
            btnIzinKeluarga.strokeWidth = 3
        }

        btnIzinDinas.setOnClickListener {
            resetSelection()
            btnIzinDinas.strokeWidth = 3
        }

        btnIzinLainnya.setOnClickListener {
            resetSelection()
            btnIzinLainnya.strokeWidth = 3
        }

        // ================= BUTTON KIRIM =================
        btnKirim.setOnClickListener {
            if (etTanggal.text.isEmpty() || etKeterangan.text.isEmpty()) {
                Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Izin berhasil diajukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
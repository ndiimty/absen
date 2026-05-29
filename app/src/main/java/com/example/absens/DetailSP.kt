package com.example.absens

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailSPActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sp)

        val header = findViewById<android.view.View>(R.id.headerLayout)
        val txtTitle = header.findViewById<TextView>(R.id.txtTitle)
        txtTitle.text = intent.getStringExtra("jenis") // 🔥 JUDUL BERUBAH

        val txtDetail = findViewById<TextView>(R.id.txtDetail)

        val jenis = intent.getStringExtra("jenis")
        val tanggal = intent.getStringExtra("tanggal")
        val alasan = intent.getStringExtra("alasan")

        txtDetail.text = """
            $jenis
            Tanggal: $tanggal
            Alasan: $alasan
        """.trimIndent()
    }
}
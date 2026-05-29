package com.example.absens

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class ReportActivity : AppCompatActivity(), DrawerController {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isThemeExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        // =========================
        // INIT DRAWER
        // =========================
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationViewUtama)

        // =========================
        // HANDLE BACK
        // =========================
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    closeDrawer()
                } else {
                    finish()
                }
            }
        })

        // =========================
        // HEADER
        // =========================
        val header = findViewById<android.view.View?>(R.id.headerLayout)
        val txtTitle = header?.findViewById<TextView>(R.id.txtTitle)
        txtTitle?.text = "REPORT"

        // =========================
        // BUTTON MENU (BUKA SIDEBAR)
        // =========================
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            openDrawer()
        }

        loadReport()

        // =========================
        // NAVBAR (5 MENU)
        // =========================
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }

        findViewById<ImageView>(R.id.navCalendar).setOnClickListener {
            startActivity(Intent(this, KalenderActivity::class.java))
        }

        findViewById<ImageView>(R.id.navCamera).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<ImageView>(R.id.navReport).setOnClickListener {
            // sudah di report
        }

        findViewById<ImageView>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // =========================
        // SIDEBAR (SAMA DASHBOARD)
        // =========================
        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_settings -> {
                    Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_password -> {
                    startActivity(Intent(this, GantiSandiActivity::class.java))
                    true
                }

                R.id.nav_theme -> {
                    isThemeExpanded = !isThemeExpanded
                    navigationView.menu.clear()
                    navigationView.inflateMenu(R.menu.sidebar_menu)
                    navigationView.menu.setGroupVisible(
                        R.id.group_theme_options,
                        isThemeExpanded
                    )
                    false
                }

                R.id.nav_light -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                    true
                }

                R.id.nav_dark -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                    true
                }

                else -> true
            }

            closeDrawer()
            true
        }
    }

    // =========================
    // DRAWER CONTROL
    // =========================
    override fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    // =========================
    // REPORT DATA (TIDAK DIUBAH)
    // =========================
    private fun loadReport() {

        val tvTelat = findViewById<TextView?>(R.id.reportTvTelat)

        val layoutTelat = findViewById<LinearLayout?>(R.id.layoutDetailTelat)
        val layoutHadir = findViewById<LinearLayout?>(R.id.layoutDetailHadir)
        val layoutLembur = findViewById<LinearLayout?>(R.id.layoutDetailLembur)
        val layoutIzin = findViewById<LinearLayout?>(R.id.layoutDetailIzin)

        val dataTelat = listOf(
            Triple("Senin, 10 Apr", 0, 15),
            Triple("Selasa, 11 Apr", 0, 30),
            Triple("Rabu, 12 Apr", 1, 10)
        )

        val dataHadir = listOf(
            "Senin - Masuk 08:00",
            "Selasa - Masuk 08:05",
            "Rabu - Masuk 07:55"
        )

        val dataLembur = listOf(
            Pair("Jumat", 2),
            Pair("Sabtu", 3)
        )

        val dataIzin = listOf(
            "Kamis - Izin",
            "Minggu - Sakit"
        )

        var totalMenit = 0
        for (item in dataTelat) {
            totalMenit += (item.second * 60) + item.third
        }

        val jam = totalMenit / 60
        val menit = totalMenit % 60

        tvTelat?.text = "Total Telat: ${dataTelat.size}x (${jam} jam ${menit} menit)"

        fun addText(layout: LinearLayout?, text: String) {
            if (layout == null) return
            val tv = TextView(this)
            tv.text = text
            tv.textSize = 14f
            tv.setPadding(0, 6, 0, 6)
            layout.addView(tv)
        }

        layoutTelat?.removeAllViews()
        for (item in dataTelat) {
            addText(layoutTelat, "${item.first} : ${item.second} jam ${item.third} menit")
        }

        layoutHadir?.removeAllViews()
        for (item in dataHadir) {
            addText(layoutHadir, item)
        }

        layoutLembur?.removeAllViews()
        for (item in dataLembur) {
            addText(layoutLembur, "${item.first} : ${item.second} jam")
        }

        layoutIzin?.removeAllViews()
        for (item in dataIzin) {
            addText(layoutIzin, item)
        }
    }
}
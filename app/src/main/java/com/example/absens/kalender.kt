package com.example.absens

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class KalenderActivity : AppCompatActivity(), DrawerController {

    private lateinit var drawerLayout: DrawerLayout
    private var isThemeExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalender)

        drawerLayout = findViewById(R.id.drawerLayout)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    closeDrawer()
                } else {
                    finish()
                }
            }
        })

        val header = findViewById<android.view.View>(R.id.headerLayout)
        header?.findViewById<TextView>(R.id.txtTitle)?.text = "KALENDER"
        header?.findViewById<ImageView>(R.id.btnMenu)?.setOnClickListener {
            openDrawer()
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val txtTanggal = findViewById<TextView>(R.id.txtTanggal)
        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            txtTanggal?.text = "Tanggal: $dayOfMonth/${month + 1}/$year"
        }

        findViewById<ImageView>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }
        findViewById<ImageView>(R.id.navCamera)?.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        findViewById<ImageView>(R.id.navReport)?.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
        findViewById<ImageView>(R.id.navProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupSidebar()
    }

    private fun setupSidebar() {
        findViewById<LinearLayout>(R.id.navItemPengaturan)?.setOnClickListener {
            Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show()
            closeDrawer()
        }
        findViewById<LinearLayout>(R.id.navItemPassword)?.setOnClickListener {
            startActivity(Intent(this, GantiSandiActivity::class.java))
            closeDrawer()
        }

        val groupTema = findViewById<LinearLayout>(R.id.groupTemaOptions)
        val iconArrow = findViewById<ImageView>(R.id.iconTemaArrow)
        findViewById<LinearLayout>(R.id.navItemTema)?.setOnClickListener {
            isThemeExpanded = !isThemeExpanded
            groupTema?.visibility = if (isThemeExpanded) android.view.View.VISIBLE else android.view.View.GONE
            iconArrow?.rotation = if (isThemeExpanded) 180f else 0f
        }

        findViewById<LinearLayout>(R.id.navItemTerang)?.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            closeDrawer()
        }
        findViewById<LinearLayout>(R.id.navItemGelap)?.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            closeDrawer()
        }
        findViewById<LinearLayout>(R.id.navItemLogout)?.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun openDrawer() = drawerLayout.openDrawer(GravityCompat.START)
    override fun closeDrawer() = drawerLayout.closeDrawer(GravityCompat.START)
}
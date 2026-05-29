package com.example.absens

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GantiSandiActivity : AppCompatActivity(), DrawerController {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var etLama: TextInputEditText
    private lateinit var etBaru: TextInputEditText
    private lateinit var etKonfirmasi: TextInputEditText
    private lateinit var btn: MaterialButton
    private lateinit var tvMatchMessage: TextView
    private lateinit var tvStrengthLabel: TextView
    private lateinit var strengthBar1: View
    private lateinit var strengthBar2: View
    private lateinit var strengthBar3: View
    private lateinit var strengthBar4: View
    private lateinit var tilLama: TextInputLayout
    private lateinit var tilBaru: TextInputLayout
    private lateinit var tilKonfirmasi: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_sandi)

        // Header title
        val header = findViewById<View?>(R.id.headerLayout)
        header?.findViewById<TextView>(R.id.txtTitle)?.text = "GANTI SANDI"

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        navView      = findViewById(R.id.navigationViewUtama)

        // Init views
        etLama          = findViewById(R.id.etPasswordLama)
        etBaru          = findViewById(R.id.etPasswordBaru)
        etKonfirmasi    = findViewById(R.id.etKonfirmasi)
        btn             = findViewById(R.id.btnGantiSandi)
        tvMatchMessage  = findViewById(R.id.tvMatchMessage)
        tvStrengthLabel = findViewById(R.id.tvStrengthLabel)
        strengthBar1    = findViewById(R.id.strengthBar1)
        strengthBar2    = findViewById(R.id.strengthBar2)
        strengthBar3    = findViewById(R.id.strengthBar3)
        strengthBar4    = findViewById(R.id.strengthBar4)
        tilLama         = findViewById(R.id.tilPasswordLama)
        tilBaru         = findViewById(R.id.tilPasswordBaru)
        tilKonfirmasi   = findViewById(R.id.tilKonfirmasi)

        setupBackPress()
        setupDrawer()
        setupNavbar()
        setupInputStrokeColors()
        setupStrengthChecker()
        setupMatchChecker()
        setupSaveButton()
    }

    // =========================
    // BACK PRESS
    // =========================
    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) closeDrawer()
                else finish()
            }
        })
    }

    // =========================
    // SIDEBAR
    // =========================
    private fun setupDrawer() {
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_password -> { /* sudah di halaman ini */ }
                R.id.nav_settings -> Toast.makeText(this, "Pengaturan", Toast.LENGTH_SHORT).show()
                R.id.nav_light    -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                R.id.nav_dark     -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            closeDrawer()
            true
        }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener { openDrawer() }
    }

    // =========================
    // NAVBAR BAWAH
    // =========================
    private fun setupNavbar() {
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java)); finish()
        }
        findViewById<ImageView>(R.id.navCalendar).setOnClickListener {
            startActivity(Intent(this, KalenderActivity::class.java))
        }
        findViewById<ImageView>(R.id.navCamera).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        findViewById<ImageView>(R.id.navReport).setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
        findViewById<ImageView>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // =========================
    // STROKE COLOR INPUT
    // =========================
    private fun setupInputStrokeColors() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(-android.R.attr.state_focused)
        )
        val colors = intArrayOf(
            Color.parseColor("#C62828"),
            Color.parseColor("#ECEDEF")
        )
        val colorStateList = ColorStateList(states, colors)

        tilLama.setBoxStrokeColorStateList(colorStateList)
        tilBaru.setBoxStrokeColorStateList(colorStateList)
        tilKonfirmasi.setBoxStrokeColorStateList(colorStateList)
    }

    // =========================
    // STRENGTH BAR
    // =========================
    private fun setupStrengthChecker() {
        etBaru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateStrengthBar(s.toString())
                if (etKonfirmasi.text?.isNotEmpty() == true) checkMatch()
            }
        })
    }

    private fun updateStrengthBar(password: String) {
        if (password.isEmpty()) {
            setAllBars("#ECEDEF")
            tvStrengthLabel.text = ""
            return
        }

        var score = 0
        if (password.length >= 8)                   score++
        if (password.any { it.isUpperCase() })      score++
        if (password.any { it.isDigit() })          score++
        if (password.any { !it.isLetterOrDigit() }) score++

        when (score) {
            1 -> {
                setBarColors("#E53935", "#ECEDEF", "#ECEDEF", "#ECEDEF")
                tvStrengthLabel.text = "Lemah"
                tvStrengthLabel.setTextColor(Color.parseColor("#E53935"))
            }
            2 -> {
                setBarColors("#FB8C00", "#FB8C00", "#ECEDEF", "#ECEDEF")
                tvStrengthLabel.text = "Sedang"
                tvStrengthLabel.setTextColor(Color.parseColor("#FB8C00"))
            }
            3 -> {
                setBarColors("#43A047", "#43A047", "#43A047", "#ECEDEF")
                tvStrengthLabel.text = "Kuat"
                tvStrengthLabel.setTextColor(Color.parseColor("#43A047"))
            }
            4 -> {
                setBarColors("#2E7D32", "#2E7D32", "#2E7D32", "#2E7D32")
                tvStrengthLabel.text = "Sangat Kuat"
                tvStrengthLabel.setTextColor(Color.parseColor("#2E7D32"))
            }
            else -> {
                setAllBars("#ECEDEF")
                tvStrengthLabel.text = ""
            }
        }
    }

    private fun setBarColors(c1: String, c2: String, c3: String, c4: String) {
        strengthBar1.setBackgroundColor(Color.parseColor(c1))
        strengthBar2.setBackgroundColor(Color.parseColor(c2))
        strengthBar3.setBackgroundColor(Color.parseColor(c3))
        strengthBar4.setBackgroundColor(Color.parseColor(c4))
    }

    private fun setAllBars(color: String) = setBarColors(color, color, color, color)

    // =========================
    // MATCH CHECKER
    // =========================
    private fun setupMatchChecker() {
        etKonfirmasi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { checkMatch() }
        })
    }

    private fun checkMatch() {
        val baru    = etBaru.text.toString()
        val konfirm = etKonfirmasi.text.toString()

        if (konfirm.isEmpty()) {
            tvMatchMessage.visibility = View.GONE
            return
        }

        tvMatchMessage.visibility = View.VISIBLE
        if (baru == konfirm) {
            tvMatchMessage.text = "✓ Kata sandi cocok"
            tvMatchMessage.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            tvMatchMessage.text = "✗ Kata sandi tidak cocok"
            tvMatchMessage.setTextColor(Color.parseColor("#C62828"))
        }
    }

    // =========================
    // TOMBOL SIMPAN
    // =========================
    private fun setupSaveButton() {
        btn.setOnClickListener {
            val lama       = etLama.text.toString().trim()
            val baru       = etBaru.text.toString().trim()
            val konfirmasi = etKonfirmasi.text.toString().trim()

            if (lama.isEmpty() || baru.isEmpty() || konfirmasi.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (baru.length < 6) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (baru != konfirmasi) {
                Toast.makeText(this, "Konfirmasi password tidak sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Hubungkan ke API / SharedPreferences di sini
            Toast.makeText(this, "✓ Password berhasil diubah!", Toast.LENGTH_SHORT).show()

            // Reset form
            etLama.text?.clear()
            etBaru.text?.clear()
            etKonfirmasi.text?.clear()
            setAllBars("#ECEDEF")
            tvStrengthLabel.text = ""
            tvMatchMessage.visibility = View.GONE
        }
    }

    // =========================
    // DRAWER CONTROL
    // =========================
    override fun openDrawer()  { drawerLayout.openDrawer(GravityCompat.START) }
    override fun closeDrawer() { drawerLayout.closeDrawer(GravityCompat.START) }
}
package com.example.absens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class Dashboard : AppCompatActivity(), DrawerController {

    private lateinit var txtJam: TextView
    private lateinit var txtTanggal: TextView
    private lateinit var txtBanner: TextView

    private lateinit var txtNamaHeader: TextView
    private lateinit var txtJabatanHeader: TextView
    private lateinit var fotoUser: ImageView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var isThemeExpanded = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val header =
            findViewById<android.view.View?>(
                R.id.headerLayout
            )

        val txtTitle =
            header?.findViewById<TextView>(
                R.id.txtTitle
            )

        txtTitle?.text = "DASHBOARD"

        // =====================
        // INIT VIEW
        // =====================

        txtJam =
            findViewById(R.id.txtJam)

        txtTanggal =
            findViewById(R.id.txtTanggal)

        txtBanner =
            findViewById(R.id.txtBanner)

        txtNamaHeader =
            findViewById(R.id.txtNamaHeader)

        txtJabatanHeader =
            findViewById(R.id.txtJabatanHeader)

        fotoUser =
            findViewById(R.id.fotoUser)

        drawerLayout =
            findViewById(R.id.drawerLayout)

        navView =
            findViewById(
                R.id.navigationViewUtama
            )

        // =====================
        // HANDLE BACK
        // =====================

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true){

                override fun handleOnBackPressed() {

                    if(
                        drawerLayout.isDrawerOpen(
                            GravityCompat.START
                        )
                    ){
                        closeDrawer()
                    }else{
                        finish()
                    }
                }
            }
        )

        // =====================
        // SIDEBAR
        // =====================

        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.nav_settings -> {

                    Toast.makeText(
                        this,
                        "Pengaturan",
                        Toast.LENGTH_SHORT
                    ).show()

                    closeDrawer()
                    true
                }

                R.id.nav_password -> {

                    startActivity(
                        Intent(
                            this,
                            GantiSandiActivity::class.java
                        )
                    )

                    closeDrawer()
                    true
                }

                R.id.nav_theme -> {

                    isThemeExpanded =
                        !isThemeExpanded

                    navView.menu.clear()

                    navView.inflateMenu(
                        R.menu.sidebar_menu
                    )

                    navView.menu.setGroupVisible(
                        R.id.group_theme_options,
                        isThemeExpanded
                    )

                    false
                }

                R.id.nav_light -> {

                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO
                        )

                    true
                }

                R.id.nav_dark -> {

                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES
                        )

                    true
                }

                R.id.nav_logout -> {

                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )

                    finish()

                    true
                }

                else -> true
            }
        }

        loadUserData()
        setupUI()
    }

    // =====================
    // LOAD USER
    // =====================

    private fun loadUserData(){

        val user =
            auth.currentUser ?: return

        val uid = user.uid

        database.reference
            .child("users")
            .child(uid)
            .get()
            .addOnSuccessListener {

                if(it.exists()){

                    val nama =
                        it.child("name")
                            .value?.toString()
                            ?: "-"

                    val penempatan =
                        it.child("penempatan")
                            .value?.toString()
                            ?: "-"

                    txtNamaHeader.text =
                        nama

                    txtJabatanHeader.text =
                        penempatan


                    // FOTO PROFIL
                    val imageUri =
                        it.child("profileImage")
                            .value?.toString()

                    if(
                        !imageUri.isNullOrEmpty()
                    ){

                        Glide.with(this)
                            .load(
                                Uri.parse(
                                    imageUri
                                )
                            )
                            .placeholder(
                                R.drawable.profil
                            )
                            .error(
                                R.drawable.profil
                            )
                            .into(
                                fotoUser
                            )

                    }else{

                        fotoUser.setImageResource(
                            R.drawable.profil
                        )
                    }
                }
            }
    }

    // =====================
    // UI
    // =====================

    private fun setupUI() {

        findViewById<ImageView>(
            R.id.btnMenu
        ).setOnClickListener {
            openDrawer()
        }

        findViewById<ImageView>(
            R.id.navHome
        ).setOnClickListener {
            closeDrawer()
        }

        findViewById<ImageView>(
            R.id.navCalendar
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    KalenderActivity::class.java
                )
            )
        }

        findViewById<ImageView>(
            R.id.navCamera
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CameraActivity::class.java
                )
            )
        }

        findViewById<ImageView>(
            R.id.navReport
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ReportActivity::class.java
                )
            )
        }

        findViewById<ImageView>(
            R.id.navProfile
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        findViewById<MaterialCardView>(
            R.id.btnIzin
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    IzinActivity::class.java
                )
            )
        }

        findViewById<MaterialCardView>(
            R.id.btnCuti
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CutiActivity::class.java
                )
            )
        }

        // Banner berjalan

        // =========================
        // MARQUEE (TEKS BERJALAN)
        // =========================

        txtBanner.apply {

            text =
                "📢 Selamat datang di aplikasi absensi • Jangan lupa absen hari ini • Disiplin adalah kunci sukses • Semangat kerja 💪 •"

            isSelected = true
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.MARQUEE
            marqueeRepeatLimit = -1

            isFocusable = true
            isFocusableInTouchMode = true

            requestFocus()
        }

        // Jam & tanggal realtime

        handler.post(object : Runnable{

            override fun run() {

                val sekarang =
                    Date()

                txtJam.text =
                    SimpleDateFormat(
                        "HH:mm:ss",
                        Locale("id","ID")
                    ).format(
                        sekarang
                    )

                txtTanggal.text =
                    SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        Locale("id","ID")
                    ).format(
                        sekarang
                    )

                handler.postDelayed(
                    this,
                    1000
                )
            }
        })
    }

    override fun openDrawer() {

        drawerLayout.openDrawer(
            GravityCompat.START
        )
    }

    override fun closeDrawer() {

        drawerLayout.closeDrawer(
            GravityCompat.START
        )
    }

    override fun onDestroy() {

        super.onDestroy()

        handler.removeCallbacksAndMessages(
            null
        )
    }
}
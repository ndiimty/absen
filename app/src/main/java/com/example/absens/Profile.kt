package com.example.absens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var imgProfile: ImageView

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvNomorHp: TextView
    private lateinit var tvPenempatan: TextView
    private lateinit var tvPenempatanDetail: TextView
    private lateinit var tvAlamat: TextView

    private lateinit var btnEditProfile: MaterialCardView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var isThemeExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // HEADER
        val header =
            findViewById<android.view.View>(
                R.id.headerLayout
            )

        header.findViewById<TextView>(
            R.id.txtTitle
        ).text = "PROFILE"

        // DRAWER
        drawerLayout =
            findViewById(R.id.drawerLayout)

        navigationView =
            findViewById(R.id.navigationViewUtama)

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<ImageView>(R.id.btnMenu)
            .setOnClickListener {

                drawerLayout.openDrawer(
                    GravityCompat.START
                )
            }

        // =====================
        // INIT VIEW XML
        // =====================

        imgProfile =
            findViewById(R.id.imgProfile)

        tvName =
            findViewById(R.id.tvName)

        tvEmail =
            findViewById(R.id.tvEmail)

        tvNomorHp =
            findViewById(R.id.tvNomorHp)

        tvPenempatan =
            findViewById(R.id.tvPenempatan)

        tvPenempatanDetail =
            findViewById(R.id.tvPenempatanDetail)

        tvAlamat =
            findViewById(R.id.tvAlamat)

        btnEditProfile =
            findViewById(R.id.btnEditProfile)

        btnEditProfile.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EditProfileActivity::class.java
                )
            )
        }

        loadProfile()

        setupDrawerMenu()
        setupNavbar()
    }

    // =====================
    // LOAD PROFILE
    // =====================

    private fun loadProfile() {

        val user = auth.currentUser ?: return
        val uid = user.uid

        database.reference
            .child("users")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                if(snapshot.exists()){

                    val name =
                        snapshot.child("name")
                            .value?.toString()
                            ?: "-"

                    val email =
                        snapshot.child("email")
                            .value?.toString()
                            ?: user.email
                            ?: "-"

                    val nomorHp =
                        snapshot.child("nomorHp")
                            .value?.toString()
                            ?: "-"

                    val penempatan =
                        snapshot.child("penempatan")
                            .value?.toString()
                            ?: "-"

                    val alamat =
                        snapshot.child("alamat")
                            .value?.toString()
                            ?: "-"

                    // SET DATA

                    tvName.text = name
                    tvEmail.text = email
                    tvNomorHp.text = nomorHp

                    // badge merah
                    tvPenempatan.text = penempatan

                    // card detail
                    tvPenempatanDetail.text = penempatan

                    tvAlamat.text = alamat
                }

                // =====================
                // FOTO DARI LOKAL
                // =====================

                val prefs =
                    getSharedPreferences(
                        "profile",
                        MODE_PRIVATE
                    )

                val imageUri =
                    prefs.getString(
                        "profileImage",
                        null
                    )

                if(!imageUri.isNullOrEmpty()){

                    Glide.with(this)
                        .load(Uri.parse(imageUri))
                        .placeholder(R.drawable.profil)
                        .error(R.drawable.profil)
                        .into(imgProfile)
                }else{
                    imgProfile.setImageResource(
                        R.drawable.profil
                    )
                }
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal memuat profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }

    // =====================
    // SIDEBAR
    // =====================

    private fun setupDrawerMenu() {

        navigationView
            .setNavigationItemSelectedListener {

                when(it.itemId){

                    R.id.nav_settings -> {

                        Toast.makeText(
                            this,
                            "Pengaturan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    R.id.nav_password -> {

                        startActivity(
                            Intent(
                                this,
                                GantiSandiActivity::class.java
                            )
                        )
                    }

                    R.id.nav_theme -> {

                        isThemeExpanded =
                            !isThemeExpanded

                        navigationView.menu.clear()

                        navigationView.inflateMenu(
                            R.menu.sidebar_menu
                        )

                        navigationView.menu
                            .setGroupVisible(
                                R.id.group_theme_options,
                                isThemeExpanded
                            )

                        return@setNavigationItemSelectedListener false
                    }

                    R.id.nav_light -> {

                        AppCompatDelegate
                            .setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                            )
                    }

                    R.id.nav_dark -> {

                        AppCompatDelegate
                            .setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                            )
                    }
                }

                drawerLayout.closeDrawer(
                    GravityCompat.START
                )

                true
            }
    }

    // =====================
    // NAVBAR
    // =====================

    private fun setupNavbar() {

        findViewById<ImageView>(R.id.navHome)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        Dashboard::class.java
                    )
                )
                finish()
            }

        findViewById<ImageView>(R.id.navCalendar)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        KalenderActivity::class.java
                    )
                )
                finish()
            }

        findViewById<ImageView>(R.id.navCamera)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        CameraActivity::class.java
                    )
                )
                finish()
            }

        findViewById<ImageView>(R.id.navReport)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        ReportActivity::class.java
                    )
                )
                finish()
            }
    }
}
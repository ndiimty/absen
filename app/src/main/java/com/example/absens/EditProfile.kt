package com.example.absens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.absens.databinding.ActivityEditProfileBinding
import com.example.absens.databinding.ContentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var contentBinding: ContentEditProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private var imageUri: Uri? = null
    private var currentImageUrl = ""

    private val PICK_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityEditProfileBinding.inflate(
                layoutInflater
            )

        setContentView(
            binding.root
        )

        contentBinding =
            binding.contentEditProfile

        auth =
            FirebaseAuth.getInstance()

        database =
            FirebaseDatabase.getInstance()

        val user =
            auth.currentUser

        if(user == null){

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
            return
        }

        // HEADER
        binding.headerLayout.txtTitle.text =
            "EDIT PROFILE"

        binding.headerLayout.btnMenu
            .setOnClickListener {

                binding.drawerLayout.openDrawer(
                    GravityCompat.START
                )
            }

        loadUserData()

        contentBinding.imgEditProfile
            .setOnClickListener {

                openGallery()
            }

        contentBinding.btnUploadPhoto
            .setOnClickListener {

                openGallery()
            }

        contentBinding.btnSave
            .setOnClickListener {

                if(validate()){

                    if(imageUri != null){

                        saveData(
                            imageUri.toString()
                        )

                    }else{

                        saveData(
                            currentImageUrl
                        )
                    }
                }
            }
    }

    // ========================
    // LOAD DATA
    // ========================

    private fun loadUserData(){

        val uid =
            auth.currentUser!!.uid

        database.reference
            .child("users")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val user =
                    auth.currentUser

                contentBinding.etName.setText(
                    snapshot.child("name")
                        .value?.toString()
                        ?: ""
                )

                contentBinding.etEmail.setText(
                    user?.email ?: ""
                )

                contentBinding.etAlamat.setText(
                    snapshot.child("alamat")
                        .value?.toString()
                        ?: ""
                )

                contentBinding.etPenempatan.setText(
                    snapshot.child("penempatan")
                        .value?.toString()
                        ?: ""
                )

                contentBinding.etNomorHp.setText(
                    snapshot.child("nomorHp")
                        .value?.toString()
                        ?: ""
                )

                currentImageUrl =
                    snapshot.child("profileImage")
                        .value?.toString()
                        ?: ""

                // PRIORITAS FOTO LOKAL
                val localImage =
                    getSharedPreferences(
                        "profile",
                        MODE_PRIVATE
                    )
                        .getString(
                            "profileImage",
                            ""
                        )

                if(!localImage.isNullOrEmpty()){

                    Glide.with(this)
                        .load(
                            Uri.parse(localImage)
                        )
                        .into(
                            contentBinding.imgEditProfile
                        )

                }
                else if(
                    currentImageUrl.isNotEmpty()
                ){

                    Glide.with(this)
                        .load(currentImageUrl)
                        .into(
                            contentBinding.imgEditProfile
                        )
                }

            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal ambil data",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ========================
    // GALERI
    // ========================

    private fun openGallery() {

        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT
        )

        intent.addCategory(
            Intent.CATEGORY_OPENABLE
        )

        intent.type = "image/*"

        startActivityForResult(
            intent,
            PICK_IMAGE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if(
            requestCode == PICK_IMAGE &&
            resultCode == Activity.RESULT_OK
        ){

            imageUri = data?.data

            imageUri?.let { uri ->

                try {

                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                } catch(_: Exception){}

                Glide.with(this)
                    .load(uri)
                    .into(
                        contentBinding.imgEditProfile
                    )

                getSharedPreferences(
                    "profile",
                    MODE_PRIVATE
                )
                    .edit()
                    .putString(
                        "profileImage",
                        uri.toString()
                    )
                    .apply()
            }
        }
    }

    // ========================
    // SAVE DATA
    // ========================

    private fun saveData(
        photoUrl:String
    ){

        val uid =
            auth.currentUser!!.uid

        val data =
            mapOf(

                "name" to
                        contentBinding.etName.text.toString(),

                "email" to
                        (auth.currentUser?.email
                            ?: ""),

                "alamat" to
                        contentBinding.etAlamat.text.toString(),

                "penempatan" to
                        contentBinding.etPenempatan.text.toString(),

                "nomorHp" to
                        contentBinding.etNomorHp.text.toString(),

                "profileImage" to
                        photoUrl
            )

        database.reference
            .child("users")
            .child(uid)
            .setValue(data)

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Profil berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        ProfileActivity::class.java
                    )
                )

                finish()

            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Gagal: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // ========================
    // VALIDASI
    // ========================

    private fun validate():Boolean{

        if(
            contentBinding.etName.text
                .toString()
                .isEmpty()
        ){

            contentBinding.etName.error =
                "Nama wajib"

            return false
        }

        if(
            contentBinding.etPenempatan.text
                .toString()
                .isEmpty()
        ){

            contentBinding.etPenempatan.error =
                "Penempatan wajib"

            return false
        }

        if(
            contentBinding.etNomorHp.text
                .toString()
                .isEmpty()
        ){

            contentBinding.etNomorHp.error =
                "Nomor HP wajib"

            return false
        }

        return true
    }
}
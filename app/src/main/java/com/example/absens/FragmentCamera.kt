package com.example.absens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Geocoder
import android.os.*
import android.view.*
import android.widget.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var btnCapture: ImageButton
    private lateinit var tvTime: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvLocation: TextView
    private lateinit var mapView: MapView
    private lateinit var stampOverlay: LinearLayout
    private lateinit var mapsCard: CardView
    private lateinit var captureOverlay: FrameLayout

    private var imageCapture: ImageCapture? = null

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private val handler = Handler(Looper.getMainLooper())

    private val clockRunnable = object : Runnable {
        override fun run() {
            val now = Date()
            tvTime.text = SimpleDateFormat("HH:mm:ss", Locale("id")).format(now)
            tvDate.text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(now)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        previewView    = view.findViewById(R.id.previewView)
        btnCapture     = view.findViewById(R.id.btnCapture)
        tvTime         = view.findViewById(R.id.tvTime)
        tvDate         = view.findViewById(R.id.tvDate)
        tvLocation     = view.findViewById(R.id.tvLocation)
        mapView        = view.findViewById(R.id.mapView)
        stampOverlay   = view.findViewById(R.id.stampOverlay)
        mapsCard       = view.findViewById(R.id.mapsCard)
        captureOverlay = view.findViewById(R.id.captureOverlay)

        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osm", 0)
        )

        mapView.setMultiTouchControls(true)

        handler.post(clockRunnable)
        startCamera()
        checkLocationPermission()

        btnCapture.setOnClickListener { takePhoto() }
    }

    // ─────────────────────────────────────────────
    // KAMERA
    // ─────────────────────────────────────────────

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(requireContext())

        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            provider.unbindAll()
            provider.bindToLifecycle(
                viewLifecycleOwner,
                CameraSelector.DEFAULT_FRONT_CAMERA,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // ─────────────────────────────────────────────
    // AMBIL FOTO
    // ─────────────────────────────────────────────

    private fun takePhoto() {
        val capture = imageCapture ?: return

        // Nonaktifkan tombol agar tidak double-tap
        btnCapture.isEnabled = false

        val tempFile = File(
            requireContext().cacheDir,
            "temp_${System.currentTimeMillis()}.jpg"
        )

        val tempOutput = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        capture.takePicture(
            tempOutput,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                        val finalBitmap = overlayBitmap(bitmap)

                        val fileName = "ABSEN_${System.currentTimeMillis()}.jpg"

                        val values = android.content.ContentValues().apply {
                            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(
                                android.provider.MediaStore.Images.Media.RELATIVE_PATH,
                                "Pictures/Absens"
                            )
                        }

                        val uri = requireContext().contentResolver.insert(
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )

                        uri?.let {
                            requireContext().contentResolver.openOutputStream(it)?.use { stream ->
                                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                            }
                        }

                        // Bersihkan file sementara & bitmap
                        tempFile.delete()
                        bitmap.recycle()
                        finalBitmap.recycle()

                        Toast.makeText(requireContext(), "Foto tersimpan di galeri", Toast.LENGTH_SHORT).show()

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        btnCapture.isEnabled = true
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                    btnCapture.isEnabled = true
                }
            }
        )
    }

    // ─────────────────────────────────────────────
    // OVERLAY BITMAP — INTI PERBAIKAN
    // ─────────────────────────────────────────────

    private fun overlayBitmap(original: Bitmap): Bitmap {

        // Foto dari kamera bisa lebih besar dari ukuran preview di layar.
        // Hitung skala: berapa piksel foto per 1 piksel layar.
        val previewW = previewView.width.takeIf { it > 0 } ?: 1
        val previewH = previewView.height.takeIf { it > 0 } ?: 1

        val scaleX = original.width.toFloat() / previewW.toFloat()
        val scaleY = original.height.toFloat() / previewH.toFloat()

        // Gunakan skala terbesar agar overlay tidak terpotong
        val scale = maxOf(scaleX, scaleY)

        // Buat canvas di atas foto asli (tidak membuat bitmap baru yang sia-sia)
        val result = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)

        // ── Render stampOverlay (teks waktu, tanggal, lokasi) ──
        val stampBitmap = renderViewToBitmap(stampOverlay)
        if (stampBitmap != null) {
            val stampScaledW = (stampOverlay.width * scale).toInt()
            val stampScaledH = (stampOverlay.height * scale).toInt()

            val stampScaled = Bitmap.createScaledBitmap(stampBitmap, stampScaledW, stampScaledH, true)

            // Posisi stamp: ambil dari margin XML (12dp start, bottom sesuai layout_gravity)
            // Konversi 12dp → piksel layar → piksel foto
            val marginStartPx = dpToPx(12) * scale
            val marginBottomPx = dpToPx(140) * scale   // layout_marginBottom stamp di XML = 140dp

            val stampLeft = marginStartPx
            val stampTop  = original.height - stampScaledH - marginBottomPx

            canvas.drawBitmap(stampScaled, stampLeft, stampTop, null)
            stampBitmap.recycle()
            stampScaled.recycle()
        }

        // ── Render mapsCard (mini map OSM) ──
        val mapBitmap = renderViewToBitmap(mapsCard)
        if (mapBitmap != null) {
            val mapScaledW = (mapsCard.width * scale).toInt()
            val mapScaledH = (mapsCard.height * scale).toInt()

            val mapScaled = Bitmap.createScaledBitmap(mapBitmap, mapScaledW, mapScaledH, true)

            // Posisi map: marginStart 12dp, marginBottom 16dp (sesuai XML)
            val marginStartPx = dpToPx(12) * scale
            val marginBottomPx = dpToPx(16) * scale

            val mapLeft = marginStartPx
            val mapTop  = original.height - mapScaledH - marginBottomPx

            canvas.drawBitmap(mapScaled, mapLeft, mapTop, null)
            mapBitmap.recycle()
            mapScaled.recycle()
        }

        return result
    }

    /**
     * Render sebuah View ke Bitmap tanpa drawingCache (sudah deprecated).
     * Menggunakan PixelCopy (API 26+) atau Canvas.draw sebagai fallback.
     */
    private fun renderViewToBitmap(view: View): Bitmap? {
        if (view.width == 0 || view.height == 0) return null

        return try {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Konversi dp ke piksel layar */
    private fun dpToPx(dp: Int): Float {
        return dp * resources.displayMetrics.density
    }

    // ─────────────────────────────────────────────
    // LOKASI
    // ─────────────────────────────────────────────

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            Toast.makeText(requireContext(), "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val geo = GeoPoint(it.latitude, it.longitude)

                mapView.controller.setZoom(18.0)
                mapView.controller.setCenter(geo)
                mapView.overlays.clear()

                val marker = Marker(mapView)
                marker.position = geo
                mapView.overlays.add(marker)

                // Geocoder modern (API 33+) pakai listener, fallback ke blocking
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Geocoder(requireContext(), Locale("id")).getFromLocation(
                        it.latitude, it.longitude, 1
                    ) { addresses ->
                        handler.post {
                            if (addresses.isNotEmpty()) {
                                tvLocation.text = addresses[0].getAddressLine(0)
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = Geocoder(requireContext(), Locale("id"))
                        .getFromLocation(it.latitude, it.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        tvLocation.text = addresses[0].getAddressLine(0)
                    }
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // LIFECYCLE
    // ─────────────────────────────────────────────

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(clockRunnable)
    }
}
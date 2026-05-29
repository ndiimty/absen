import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import android.content.res.ColorStateList
import com.example.absens.databinding.BottomNavMenuBinding

// ⚠️ GANTI ini sesuai package dan binding Activity kamu
// Contoh: jika file layout kamu adalah activity_home.xml
// maka bindingnya adalah ActivityHomeBinding
// import com.example.namaapp.databinding.ActivityHomeBinding

class MainActivity : AppCompatActivity() {

    // ⚠️ GANTI ActivityMainBinding sesuai nama layout Activity kamu
    // Contoh: ActivityHomeBinding, ActivityDashboardBinding, dll
    private lateinit var binding: BottomNavMenuBinding

    // ── Navbar views ──────────────────────────────────────────────
    // Jika navbar di-include di layout, akses via binding.namaInclude.navHome
    // Jika langsung di layout (tidak pakai <include>), akses langsung binding.navHome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomNavMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavbar()
    }

    private fun setupNavbar() {
        // ── Kumpulkan semua view navbar ───────────────────────────
        // Jika navbar pakai <include android:id="@+id/navbar" ...>
        // ganti binding.navHome → binding.navbar.navHome, dst.

        val navHome:     ImageView   = binding.navHome
        val navCalendar: ImageView   = binding.navCalendar
        val navReport:   ImageView   = binding.navReport
        val navProfile:  ImageView   = binding.navProfile

        val tvNavHome:     TextView  = binding.tvNavHome
        val tvNavCalendar: TextView  = binding.tvNavCalendar
        val tvNavReport:   TextView  = binding.tvNavReport
        val tvNavProfile:  TextView  = binding.tvNavProfile

        val navHomeWrapper:     LinearLayout = binding.navHomeWrapper
        val navCalendarWrapper: LinearLayout = binding.navCalendarWrapper
        val navReportWrapper:   LinearLayout = binding.navReportWrapper
        val navProfileWrapper:  LinearLayout = binding.navProfileWrapper
        val navCameraWrapper:   LinearLayout = binding.navCameraWrapper

        val navDotIndicator: View = binding.navDotIndicator

        // ── Fungsi setActive ──────────────────────────────────────
        val RED  = "#CC0000"
        val GREY = "#AAAAAA"

        data class NavItem(val icon: ImageView, val label: TextView, val tag: String)

        val items = listOf(
            NavItem(navHome,     tvNavHome,     "home"),
            NavItem(navCalendar, tvNavCalendar, "calendar"),
            NavItem(navReport,   tvNavReport,   "report"),
            NavItem(navProfile,  tvNavProfile,  "profile")
        )

        fun setActive(activeTag: String) {
            items.forEach { item ->
                val isActive = item.tag == activeTag
                val color = if (isActive) RED else GREY
                ImageViewCompat.setImageTintList(
                    item.icon,
                    ColorStateList.valueOf(Color.parseColor(color))
                )
                item.label.setTextColor(Color.parseColor(color))
                item.label.setTypeface(null, if (isActive) Typeface.BOLD else Typeface.NORMAL)
            }
            navDotIndicator.visibility =
                if (activeTag == "home") View.VISIBLE else View.GONE
        }

        // ── Set halaman aktif saat ini ────────────────────────────
        // Ganti "home" sesuai halaman ini
        setActive("home")

        // ── Click listener ────────────────────────────────────────
        navHomeWrapper.setOnClickListener {
            setActive("home")
            // TODO: pindah ke halaman Home
        }
        navCalendarWrapper.setOnClickListener {
            setActive("calendar")
            // TODO: pindah ke halaman Kalender
        }
        navCameraWrapper.setOnClickListener {
            // Kamera tidak ada active state
            // TODO: buka kamera
        }
        navReportWrapper.setOnClickListener {
            setActive("report")
            // TODO: pindah ke halaman Laporan
        }
        navProfileWrapper.setOnClickListener {
            setActive("profile")
            // TODO: pindah ke halaman Profil
        }
    }
}
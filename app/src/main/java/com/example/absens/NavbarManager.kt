import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import android.content.res.ColorStateList

/**
 * NavbarManager
 * Cara pakai:
 *   1. Include navbar XML di layout Activity/Fragment kamu
 *   2. Buat instance NavbarManager dan isi semua view-nya
 *   3. Panggil setActive() sesuai halaman yang sedang dibuka
 */
class NavbarManager(
    private val navHome:     ImageView,
    private val navCalendar: ImageView,
    private val navReport:   ImageView,
    private val navProfile:  ImageView,

    private val tvNavHome:     TextView,
    private val tvNavCalendar: TextView,
    private val tvNavReport:   TextView,
    private val tvNavProfile:  TextView,

    private val navHomeWrapper:     LinearLayout,
    private val navCalendarWrapper: LinearLayout,
    private val navReportWrapper:   LinearLayout,
    private val navProfileWrapper:  LinearLayout,
    private val navCameraWrapper:   LinearLayout,

    private val navDotIndicator: View
) {
    enum class Menu { HOME, CALENDAR, CAMERA, REPORT, PROFILE }

    private val RED  = "#CC0000"
    private val GREY = "#AAAAAA"

    private val items = listOf(
        Triple(navHome,     tvNavHome,     Menu.HOME),
        Triple(navCalendar, tvNavCalendar, Menu.CALENDAR),
        Triple(navReport,   tvNavReport,   Menu.REPORT),
        Triple(navProfile,  tvNavProfile,  Menu.PROFILE)
    )

    /** Tandai menu yang aktif — ubah warna ikon + label + dot */
    fun setActive(menu: Menu) {
        items.forEach { (icon, label, item) ->
            val isActive = item == menu
            val color = if (isActive) RED else GREY

            ImageViewCompat.setImageTintList(
                icon, ColorStateList.valueOf(Color.parseColor(color))
            )
            label.setTextColor(Color.parseColor(color))
            label.setTypeface(
                null,
                if (isActive) Typeface.BOLD else Typeface.NORMAL
            )
        }
        navDotIndicator.visibility =
            if (menu == Menu.HOME) View.VISIBLE else View.GONE
    }

    /** Daftarkan semua click listener sekaligus */
    fun setupClicks(onMenuClick: (Menu) -> Unit) {
        navHomeWrapper.setOnClickListener     { onMenuClick(Menu.HOME) }
        navCalendarWrapper.setOnClickListener { onMenuClick(Menu.CALENDAR) }
        navCameraWrapper.setOnClickListener   { onMenuClick(Menu.CAMERA) }
        navReportWrapper.setOnClickListener   { onMenuClick(Menu.REPORT) }
        navProfileWrapper.setOnClickListener  { onMenuClick(Menu.PROFILE) }
    }
}
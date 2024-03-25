package nonetheless.prayertime.model

import nonetheless.prayertime.R
import java.util.Calendar

enum class PrayerName(val painter:Int, val painterDescription:Int) {
    Fajr(R.drawable.dawn,R.string.dawn_content_description),
    Dhuhr(R.drawable.noon,R.string.noon_content_description),
    Asr(R.drawable.afternoon,R.string.afternoon_content_description),
    Maghrib(R.drawable.sunset,R.string.sunset_content_description),
    Isha(R.drawable.night,R.string.night_content_description)
}
data class Prayer(val name:PrayerName, val time: Calendar)

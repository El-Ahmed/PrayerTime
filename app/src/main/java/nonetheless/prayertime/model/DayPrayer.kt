package nonetheless.prayertime.model

import java.util.Calendar

data class DayPrayer(val city: City, val day: Calendar, val hijriDate: HijriDate, val prayers: List<Prayer>)
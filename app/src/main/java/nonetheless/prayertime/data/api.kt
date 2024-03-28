package nonetheless.prayertime.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nonetheless.prayertime.model.City
import nonetheless.prayertime.model.DayPrayer
import nonetheless.prayertime.model.HijriDate
import nonetheless.prayertime.model.Prayer
import nonetheless.prayertime.model.PrayerName
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.Calendar


val API_LINK = "https://habous.gov.ma/prieres/horaire_hijri_2.php?ville="
suspend fun getCurrentMonthDocument(city: City): Document = withContext(Dispatchers.IO) {
    return@withContext Jsoup.connect(API_LINK + city.id).validateTLSCertificates(false).get()
}

suspend fun getCurrentMonthPrayer(city: City): List<DayPrayer> {
    val document = getCurrentMonthDocument(city)
    val prayerLines = getPrayerLines(document)
    val today = Calendar.getInstance()
    val todayIndex = prayerLines.indexOfFirst { line ->
        return@indexOfFirst line[2].toIntOrNull() == today.get(Calendar.DAY_OF_MONTH)
    }
    val endIndex = prayerLines.subList(1, prayerLines.lastIndex + 1).indexOfFirst { line ->
        return@indexOfFirst line[1].toIntOrNull() == null
    }
    val fixedEndIndex = if (endIndex > 0) endIndex else prayerLines.lastIndex + 1

    val hijriMonth = prayerLines[0][1]
    return prayerLines.subList(todayIndex, fixedEndIndex).mapIndexed { index, line ->
        val todayCalendar = today.clone() as Calendar
        todayCalendar.add(Calendar.DAY_OF_MONTH, index)
        prayerLineToDayPrayer(line, hijriMonth, city, todayCalendar)
    }

}

/**
 * time is HH:mm
 */
fun stringTimeToCalendar(calendar: Calendar, time: String): Calendar {
    val calendarClone = calendar.clone() as Calendar
    val timeList = time.split(":").map { it.toInt() }

    calendarClone.set(Calendar.HOUR_OF_DAY, timeList[0])
    calendarClone.set(Calendar.MINUTE, timeList[1])
    return calendarClone
}

fun prayerLineToDayPrayer(
    line: List<String>,
    hijriMonth: String,
    city: City,
    calendar: Calendar
): DayPrayer {
    val fajr = Prayer(PrayerName.Fajr, stringTimeToCalendar(calendar, line[3]))
    val dhuhr = Prayer(PrayerName.Dhuhr, stringTimeToCalendar(calendar, line[5]))
    val asr = Prayer(PrayerName.Asr, stringTimeToCalendar(calendar, line[6]))
    val maghrib = Prayer(PrayerName.Maghrib, stringTimeToCalendar(calendar, line[7]))
    val isha = Prayer(PrayerName.Isha, stringTimeToCalendar(calendar, line[8]))

    return DayPrayer(
        0,
        city,
        calendar,
        HijriDate(hijriMonth, line[1].toInt()),
        listOf(fajr, dhuhr, asr, maghrib, isha)
    )
}

fun getPrayerLines(document: Document): List<List<String>> {
    val table = document.getElementById("horaire")
    val tableBody = table.child(0)
    return tableBody.children().map { line ->
        line.children().map { column ->
            column.text()
        }
    }
}
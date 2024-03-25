package nonetheless.prayertime.ui.composables

import android.icu.text.Transliterator
import android.icu.util.IslamicCalendar
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nonetheless.prayertime.model.DayOfWeekString
import nonetheless.prayertime.model.HijriDate
import java.text.SimpleDateFormat
import java.util.Calendar


@RequiresApi(Build.VERSION_CODES.Q)
fun arabicMonthToLatin(arabicMonth: String): String {
    if (arabicMonth.contains("م")) {
        if (arabicMonth.contains("ح")) return "Muharram"
        if (arabicMonth.contains("ض")) return "Ramadan"
        if (arabicMonth.contains("و")) return "Jumada al-Awwal"
        return "Jumada al-Thani"
    }
    if (arabicMonth.contains("ف")) return "Safar"
    if (arabicMonth.contains("ش")) {
        if (arabicMonth.contains("ع")) return "Sha'ban"
        return "Shawwal"
    }
    if (arabicMonth.contains("ذ") || arabicMonth.contains("د")) {
        if (arabicMonth.contains("ق")) return "Dhu al-Qadah"
        return "Dhu al-Hijja"
    }
    if (arabicMonth.contains("ب")) return "Rajab"
    if (arabicMonth.contains("ع")) {
        if (arabicMonth.contains("و")) return "Rabi' al-Awwal"
        return "Rabi' al-Thani"

    }

    val transliterator =
        Transliterator.getInstance("Arabic-Latin; nfd; [:nonspacing mark:] remove; nfc")
    return transliterator.transliterate(arabicMonth).split(" ")
        .joinToString(" ") { it.replaceFirstChar { firstLetter -> firstLetter.titlecase() } }
}

fun calculateHijriYear(): Int {
    val locale = ULocale("@calendar=islamic")
    val islamicCalendar = IslamicCalendar.getInstance(locale)
    return islamicCalendar.get(IslamicCalendar.YEAR)
}


private val formatter = SimpleDateFormat("MMMM dd, yyyy")

@Composable
fun DatePreview(hijriDate: HijriDate, calendar: Calendar) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = DayOfWeekString.entries[calendar.get(Calendar.DAY_OF_WEEK) - 1].stringResource),
                modifier = Modifier.padding(end = 8.dp),
                fontSize = 14.sp
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Text(
                text = "${hijriDate.day} ${arabicMonthToLatin(hijriDate.month)} ${calculateHijriYear()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(text = formatter.format(calendar.time))
    }
}


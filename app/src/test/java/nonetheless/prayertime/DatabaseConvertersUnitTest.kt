package nonetheless.prayertime

import nonetheless.prayertime.data.Converters
import nonetheless.prayertime.model.HijriDate
import nonetheless.prayertime.model.Prayer
import nonetheless.prayertime.model.PrayerName
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class DatabaseConvertersUnitTest {

    val converters = Converters()
    @Test
    fun sameDayCalendars_areEqual() {

        val calendar = Calendar.getInstance()
        val calendar2 = calendar.clone() as Calendar
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar2.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 8)
        calendar2.set(Calendar.MINUTE, 25)
        calendar.set(Calendar.SECOND, 0)
        calendar2.set(Calendar.SECOND, 2)


        val timestamp = converters.toTimestamp(calendar)
        val timestamp2 = converters.toTimestamp(calendar2)

        Assert.assertEquals(timestamp, timestamp2)

        val convertedCalendar = converters.fromTimestamp(timestamp)
        val convertedCalendar2 = converters.fromTimestamp(timestamp2)
        Assert.assertEquals(convertedCalendar, convertedCalendar2)
    }

    @Test
    fun differentDayCalendars_areNotEqual() {

        val calendar = Calendar.getInstance()
        val calendar2 = calendar.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar2.set(Calendar.DAY_OF_MONTH, 2)


        val timestamp = converters.toTimestamp(calendar)
        val timestamp2 = converters.toTimestamp(calendar2)

        Assert.assertNotEquals(timestamp, timestamp2)

        val convertedCalendar = converters.fromTimestamp(timestamp)
        val convertedCalendar2 = converters.fromTimestamp(timestamp2)
        Assert.assertNotEquals(convertedCalendar, convertedCalendar2)
    }

    @Test
    fun hijriDate_isConvertedBack() {

        val hijriDate = HijriDate("رمضان", 1)

        val stringValue:String? = converters.hijriDateToString(hijriDate)

        val convertedHijriDate = converters.stringToHijriDate(stringValue)

        Assert.assertEquals(hijriDate, convertedHijriDate)

    }

    @Test
    fun prayersList_isConvertedBack() {

        val fajrTime = Calendar.getInstance()
        fajrTime.set(Calendar.HOUR_OF_DAY, 4)
        fajrTime.set(Calendar.MINUTE, 45)

        val ishaTime = fajrTime.clone() as Calendar
        ishaTime.set(Calendar.HOUR_OF_DAY, 20)
        ishaTime.set(Calendar.MINUTE, 25)
        val prayers = listOf(Prayer(PrayerName.Fajr, fajrTime ), Prayer(PrayerName.Isha, ishaTime))
        val stringValue = converters.prayersToString(prayers)
        val convertedPrayer = converters.stringToPrayers(stringValue)

        Assert.assertEquals(prayers, convertedPrayer)

    }

}
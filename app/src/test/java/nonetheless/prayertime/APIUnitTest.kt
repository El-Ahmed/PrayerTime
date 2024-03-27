package nonetheless.prayertime

import kotlinx.coroutines.runBlocking
import nonetheless.prayertime.data.getCurrentMonthDocument
import nonetheless.prayertime.data.getPrayerLines
import nonetheless.prayertime.data.prayerLineToDayPrayer
import nonetheless.prayertime.data.stringTimeToCalendar
import nonetheless.prayertime.model.City
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar

class APIUnitTest {

    @Test
    fun correctData_isFetched() {
        val document = runBlocking {
             return@runBlocking getCurrentMonthDocument(City.Rabat)
        }

        val selectElement = document.select("select[name=ville]").first()
        val selectedOptionValue = selectElement.select("option[selected]").first().attr("value")
        Assert.assertEquals("horaire_hijri_2.php?ville=1", selectedOptionValue)
    }


    @Test
    fun casablanca_isFetched() {
        val document = runBlocking {
            return@runBlocking getCurrentMonthDocument(City.Casablanca)
        }

        val selectElement = document.select("select[name=ville]").first()
        val selectedOptionValue = selectElement.select("option[selected]").first().attr("value")
        Assert.assertEquals("horaire_hijri_2.php?ville=58", selectedOptionValue)
    }

    @Test
    fun prayerLines_areExtracted() {
        val document = runBlocking {
            return@runBlocking getCurrentMonthDocument(City.Casablanca)
        }
        val prayerLines = getPrayerLines(document)
        Assert.assertEquals(1, prayerLines[1][1].toInt())
    }

    @Test
    fun stringTime_isConverted() {
        val stringTime = "15:58"
        val formatter = SimpleDateFormat("HH:mm")
        val newStringTime = formatter.format(stringTimeToCalendar(Calendar.getInstance(), stringTime).time)
        Assert.assertEquals(stringTime, newStringTime)
    }

    @Test
    fun prayerLine_isConverted() {
        val formatter = SimpleDateFormat("HH:mm")

        val line = listOf("السبت", "5", "16", "05:05", "06:26", "12:37", "15:58", "18:40", "19:51")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, Calendar.MARCH)
        calendar.set(Calendar.DAY_OF_MONTH, 16)
        calendar.set(Calendar.YEAR, 2024)
        val dayPrayer = prayerLineToDayPrayer(line, "رمضان",  City.Casablanca, calendar)

        listOf("05:05", "12:37", "15:58", "18:40", "19:51").forEachIndexed { index, value->
            Assert.assertEquals(value,formatter.format(dayPrayer.prayers[index].time.time))
        }
        Assert.assertEquals(5, dayPrayer.hijriDate.day)
    }
}
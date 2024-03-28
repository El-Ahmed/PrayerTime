package nonetheless.prayertime.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import nonetheless.prayertime.model.DayPrayer
import nonetheless.prayertime.model.HijriDate
import nonetheless.prayertime.model.Prayer
import nonetheless.prayertime.model.PrayerName
import java.util.Calendar


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {

        return value?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            return calendar
        }
    }

    @TypeConverter
    fun toTimestamp(date: Calendar?): Long? {
        return date?.let {
            val calendar = it.clone() as Calendar
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            return calendar.timeInMillis
        }
    }

    @TypeConverter
    fun hijriDateToString(hijriDate: HijriDate?): String? {
        return hijriDate?.let {
            return "${it.day}:${it.month}"
        }
    }

    @TypeConverter
    fun stringToHijriDate(value: String?): HijriDate? {
        return value?.let {
            val values = value.split(":")
            return HijriDate(values[1], values[0].toInt())
        }
    }

    @TypeConverter
    fun prayersToString(prayers: List<Prayer>?): String? {
        return prayers?.joinToString("-") {
            "${it.name.name}:${it.time.timeInMillis}"
        }
    }

    @TypeConverter
    fun stringToPrayers(value: String?): List<Prayer>? {
        return value?.split("-")?.map {
            val values = it.split(":")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = values[1].toLong()
            Prayer(PrayerName.valueOf(values[0]), calendar)
        }
    }
}

@Database(entities = [DayPrayer::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dayPrayerDao(): DayPrayerDao
}

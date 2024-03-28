package nonetheless.prayertime.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import nonetheless.prayertime.model.City
import nonetheless.prayertime.model.DayPrayer
import java.util.Calendar



@Dao
interface DayPrayerDao {
    @Query(
        "SELECT * FROM dayprayer WHERE city = :currentCity AND " +
                "day LIKE :currentDay LIMIT 1"
    )
    suspend fun findByCityAndDate(currentCity: City, currentDay: Calendar): DayPrayer?

    @Insert
    suspend fun insertAll(vararg dayPrayers: DayPrayer)
}



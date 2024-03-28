package nonetheless.prayertime.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class DayPrayer(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val city: City,
    @ColumnInfo val day: Calendar,
    @ColumnInfo val hijriDate: HijriDate,
    @ColumnInfo val prayers: List<Prayer>
)
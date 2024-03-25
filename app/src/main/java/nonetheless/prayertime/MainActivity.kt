package nonetheless.prayertime

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nonetheless.prayertime.data.getSelectedCityFlow
import nonetheless.prayertime.data.setSelectedCity
import nonetheless.prayertime.model.City
import nonetheless.prayertime.model.HijriDate
import nonetheless.prayertime.model.Prayer
import nonetheless.prayertime.model.PrayerName
import nonetheless.prayertime.ui.composables.DatePreview
import nonetheless.prayertime.ui.composables.LocationPreview
import nonetheless.prayertime.ui.composables.PrayerPreview
import nonetheless.prayertime.ui.composables.PrayersList
import nonetheless.prayertime.ui.theme.PrayerTimeTheme
import java.util.Calendar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val calendar = Calendar.getInstance()
        val fajrTime = Calendar.getInstance()
        fajrTime.set(Calendar.HOUR_OF_DAY, 4)
        fajrTime.set(Calendar.MINUTE, 4)
        val dhuhrTime = Calendar.getInstance()
        dhuhrTime.set(Calendar.HOUR_OF_DAY, 12)
        dhuhrTime.set(Calendar.MINUTE, 34)
        val asrTime = Calendar.getInstance()
        asrTime.set(Calendar.HOUR_OF_DAY, 16)
        asrTime.set(Calendar.MINUTE, 3)
        val maghribTime = Calendar.getInstance()
        maghribTime.set(Calendar.HOUR_OF_DAY, 18)
        maghribTime.set(Calendar.MINUTE, 41)
        val ishaTime = Calendar.getInstance()
        ishaTime.set(Calendar.HOUR_OF_DAY, 20)
        ishaTime.set(Calendar.MINUTE, 0)
        val prayers = listOf(
            Prayer(PrayerName.Fajr, fajrTime),
            Prayer(PrayerName.Dhuhr, dhuhrTime),
            Prayer(PrayerName.Asr, asrTime),
            Prayer(PrayerName.Maghrib, maghribTime),
            Prayer(PrayerName.Isha, ishaTime),
        )
        setContent {
            PrayerTimeTheme {
                MainView(
                    calendar,
                    HijriDate("رمضان", 11),
                    prayers,
                    applicationContext
                )
            }
        }
    }

}


fun getCurrentPrayerIndex(calendar: Calendar, prayers: List<Prayer>): Int {
    val currentTime = calendar.timeInMillis
    if (prayers.all {
            val distanceInMin = (it.time.timeInMillis - currentTime) / 60_000
            distanceInMin <= -5
        }) return prayers.lastIndex
    return prayers.withIndex().minBy {
        val distanceInMin = (it.value.time.timeInMillis - currentTime) / 60_000
        if (distanceInMin > -5) distanceInMin
        else Long.MAX_VALUE
    }.index
}


@Composable
fun MainView(calendar: Calendar, hijriDate: HijriDate, prayers: List<Prayer>, context: Context) {
    var selectedIndex by remember { mutableIntStateOf(getCurrentPrayerIndex(calendar, prayers)) }
    var selectedCity by remember { mutableStateOf(City.Rabat) }

    LaunchedEffect(Unit) {
        getSelectedCityFlow(context = context).collect { selectedCity = it }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                selectedIndex = getCurrentPrayerIndex(calendar, prayers)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp, bottom = 52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                LocationPreview(selectedCity, onSelectedCityChange = {
                    val scope = CoroutineScope(Job())
                    scope.launch {
                        setSelectedCity(context, it)
                    }
                })
                Spacer(modifier = Modifier.height(8.dp))
                PrayerPreview(prayers[selectedIndex])
            }
            Column {
                DatePreview(hijriDate = hijriDate, calendar = calendar)
                Spacer(modifier = Modifier.height(24.dp))
                PrayersList(prayers, selectedIndex, onSelectedPrayer = { selectedIndex = it })
            }
        }
    }
}

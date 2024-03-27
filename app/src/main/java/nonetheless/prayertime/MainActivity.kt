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
import nonetheless.prayertime.data.getCurrentMonthPrayer
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
        setContent {
            PrayerTimeTheme {
                MainView(
                    calendar, applicationContext
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
fun MainView(calendar: Calendar, context: Context) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    var selectedCity by remember { mutableStateOf(City.Rabat) }
    var todayPrayers: List<Prayer> by remember { mutableStateOf(emptyList()) }
    var hijriDate: HijriDate? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        getSelectedCityFlow(context = context).collect {
            selectedCity = it
            val currentMonthPrayer = getCurrentMonthPrayer(it)
            val todayPrayer = currentMonthPrayer.find { dayPrayer ->
                calendar.get(Calendar.YEAR) == dayPrayer.day.get(Calendar.YEAR) && calendar.get(
                    Calendar.MONTH
                ) == dayPrayer.day.get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == dayPrayer.day.get(
                    Calendar.DAY_OF_MONTH
                )
            }
            todayPrayers = todayPrayer?.prayers ?: emptyList()
            selectedIndex = getCurrentPrayerIndex(calendar, todayPrayers)
            hijriDate = todayPrayer?.hijriDate
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                selectedIndex = getCurrentPrayerIndex(calendar, todayPrayers)
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
                        todayPrayers = emptyList()
                        hijriDate = null
                        setSelectedCity(context, it)
                    }
                })
                Spacer(modifier = Modifier.height(8.dp))
                PrayerPreview(prayer = if (todayPrayers.isNotEmpty()) todayPrayers[selectedIndex] else null)
            }
            Column {
                DatePreview(hijriDate = hijriDate, calendar = calendar)
                Spacer(modifier = Modifier.height(24.dp))
                PrayersList(todayPrayers, selectedIndex, onSelectedPrayer = { selectedIndex = it })
            }
        }
    }
}

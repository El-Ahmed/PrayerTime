package nonetheless.prayertime.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nonetheless.prayertime.model.City


val Context.dataStore by preferencesDataStore(
    name = "settings"
)


val SELECTED_CITY = stringPreferencesKey("selected_city")
fun getSelectedCityFlow(context: Context): Flow<City>{
    return context.dataStore.data
        .map { preferences ->
            // No type safety.
            City.valueOf(preferences[SELECTED_CITY] ?: City.Rabat.name)
        }
}

suspend fun setSelectedCity(context: Context, city: City) {
    context.dataStore.edit { settings ->
        settings[SELECTED_CITY] = city.name
    }
}
